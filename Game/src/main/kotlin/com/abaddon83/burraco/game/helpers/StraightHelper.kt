package com.abaddon83.burraco.game.helpers

import com.abaddon83.burraco.common.models.card.Rank
import com.abaddon83.burraco.common.models.card.Suit
import com.abaddon83.burraco.game.models.Straight
import com.abaddon83.burraco.common.models.card.Card
import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log

object StraightHelper {

    fun validStraight(cards: List<Card>): Boolean {
        if (cards.size < 3) {
            log.warn("A Straight is composed by 3 or more cards")
            return false
        }
        return try {
            val suit = calculateStraightSuit(cards)
            buildSortedSequence(cards, suit)
            true
        } catch (ex: Exception) {
            log.warn(ex.message)
            false
        }
    }

    fun sortStraight(straight: Straight): Straight {
        val suit = calculateStraightSuit(straight.cards)
        val sortedCards = buildSortedSequence(straight.cards, suit)
        return straight.copy(cards = sortedCards)
    }

    private fun calculateStraightSuit(cards: List<Card>): Suit {
        val cardsBySuit = cards.filter { it.rank != Rank.Jolly }
            .groupBy { it.suit }
            .mapValues { (_, v) -> v.size }
        
        require(cardsBySuit.keys.size <= 2) { "Too many different suits found: ${cardsBySuit.keys}" }
        
        val primarySuit = cardsBySuit.maxByOrNull { it.value }!!.key
        if (cardsBySuit.keys.size == 2) {
            val numCardsSecondarySuit = cardsBySuit.minByOrNull { it.value }!!.value
            require(numCardsSecondarySuit == 1) { "Found $numCardsSecondarySuit with not a $primarySuit suit, max allowed is 1" }
        }
        return primarySuit
    }

    private fun buildSortedSequence(cards: List<Card>, primarySuit: Suit): List<Card> {
        // Separate cards by type
        val jollies = cards.filter { it.rank == Rank.Jolly }
        val aces = cards.filter { it.rank == Rank.Ace }
        val twos = cards.filter { it.rank == Rank.Two }
        val otherCards = cards.filter { it.rank != Rank.Jolly && it.rank != Rank.Ace && it.rank != Rank.Two }

        // Identify wild cards: jollies + non-primary suit twos
        val wildTwos = twos.filter { it.suit != primarySuit }
        val allWildCards = jollies + wildTwos
        require(allWildCards.size <= 1) { "Too many wild cards found: ${allWildCards.size}, max allowed 1" }

        // Natural cards: primary suit twos + other cards
        val naturalTwos = twos.filter { it.suit == primarySuit }
        val naturalCards = otherCards + naturalTwos

        // Build position map for natural cards
        val positionMap = mutableMapOf<Int, MutableList<Card>>()
        naturalCards.forEach { card ->
            positionMap.getOrPut(card.rank.position) { mutableListOf() }.add(card)
        }

        // Handle ace placement
        if (aces.isNotEmpty()) {
            val occupiedPositions = positionMap.keys.sorted()
            if (occupiedPositions.isNotEmpty()) {
                val minPos = occupiedPositions.min()
                val maxPos = occupiedPositions.max()
                
                // Determine if Ace should go high or low based on sequence continuity
                // If we have a continuous sequence from low positions (2-3), Ace goes low
                // If we have a continuous sequence to high positions (Q-K), Ace goes high
                val hasLowSequenceStart = minPos == 2 && (3 in positionMap) // We have 2-3 sequence
                val hasHighSequenceEnd = maxPos >= 12 && (maxPos == 13 || (12 in positionMap && 13 in positionMap)) // We have Q-K or ending with Q/K
                
                when {
                    hasLowSequenceStart -> {
                        // Ace is part of A-2-3... sequence, keep it low
                        positionMap.getOrPut(1) { mutableListOf() }.addAll(aces)
                    }
                    hasHighSequenceEnd -> {
                        // Ace extends J-Q-K-A sequence, place it high
                        positionMap.getOrPut(14) { mutableListOf() }.addAll(aces)
                    }
                    maxPos >= 12 -> {
                        // We have Q or higher but no clear high sequence - check for gap
                        if (13 in positionMap) {
                            // We have K, so Ace can go high
                            positionMap.getOrPut(14) { mutableListOf() }.addAll(aces)
                        } else {
                            // No K present, keep Ace low to avoid gap
                            positionMap.getOrPut(1) { mutableListOf() }.addAll(aces)
                        }
                    }
                    minPos <= 3 -> {
                        // We have 2 or 3, Ace likely goes low
                        positionMap.getOrPut(1) { mutableListOf() }.addAll(aces)
                    }
                    else -> {
                        // Default to low end if unclear
                        positionMap.getOrPut(1) { mutableListOf() }.addAll(aces)
                    }
                }
            }
        }

        // Handle special case: duplicate same-suit cards where one acts as wild
        if (allWildCards.isEmpty()) {
            // Look for positions with multiple cards of same suit - one might act as wild
            val duplicatePositions = positionMap.filter { (_, cards) -> 
                cards.size > 1 && cards.all { it.suit == primarySuit }
            }
            
            if (duplicatePositions.isNotEmpty()) {
                // Find gaps to determine which duplicate acts as wild
                val allPositions = positionMap.keys.sorted()
                val minPos = allPositions.min()
                val maxPos = allPositions.max()
                
                // Look for gaps that need filling (including potential extensions)
                var gapFound = false
                
                // First check for gaps within the existing range
                for (pos in minPos..maxPos) {
                    if (pos !in positionMap) {
                        // Find a duplicate card to use as wild for this position
                        val duplicateEntry = duplicatePositions.entries.firstOrNull()
                        if (duplicateEntry != null) {
                            val (dupPos, dupCards) = duplicateEntry
                            if (dupCards.size >= 2) {
                                // Move one card from duplicate position to gap position
                                val wildCard = dupCards.removeAt(0)
                                positionMap.getOrPut(pos) { mutableListOf() }.add(wildCard)
                                gapFound = true
                                break
                            }
                        }
                    }
                }
                
                // If no gap found within range, check if we need to extend the sequence
                if (!gapFound && duplicatePositions.isNotEmpty()) {
                    val duplicateEntry = duplicatePositions.entries.firstOrNull()
                    if (duplicateEntry != null) {
                        val (dupPos, dupCards) = duplicateEntry
                        if (dupCards.size >= 2) {
                            // Try extending upward first (high end), then downward (low end)
                            val upperExtension = maxPos + 1
                            val lowerExtension = minPos - 1
                            
                            if (upperExtension <= 14) {
                                val wildCard = dupCards.removeAt(0)
                                positionMap.getOrPut(upperExtension) { mutableListOf() }.add(wildCard)
                            } else if (lowerExtension >= 1) {
                                val wildCard = dupCards.removeAt(0)
                                positionMap.getOrPut(lowerExtension) { mutableListOf() }.add(wildCard)
                            }
                        }
                    }
                }
            }
        } else {
            // Place wild cards in gaps
            val allPositions = positionMap.keys.sorted()
            if (allPositions.isNotEmpty()) {
                val minPos = allPositions.min()
                val maxPos = allPositions.max()
                
                var wildPlaced = false
                // First try to fill gaps within the sequence
                for (pos in minPos..maxPos) {
                    if (pos !in positionMap) {
                        positionMap[pos] = mutableListOf<Card>().apply { addAll(allWildCards) }
                        wildPlaced = true
                        break
                    }
                }
                
                // If no gap found, extend sequence
                if (!wildPlaced) {
                    // Try extending upward first, then downward
                    val upperExtension = maxPos + 1
                    val lowerExtension = minPos - 1
                    
                    if (upperExtension <= 14) {
                        positionMap[upperExtension] = mutableListOf<Card>().apply { addAll(allWildCards) }
                    } else if (lowerExtension >= 1) {
                        positionMap[lowerExtension] = mutableListOf<Card>().apply { addAll(allWildCards) }
                    } else {
                        throw AssertionError("Cannot place wild card in valid position")
                    }
                }
            } else {
                // Only wild cards and aces
                positionMap[2] = mutableListOf<Card>().apply { addAll(allWildCards) }
            }
        }

        // Verify no gaps remain
        val finalPositions = positionMap.keys.sorted()
        val actualMinPos = finalPositions.min()
        val actualMaxPos = finalPositions.max()
        
        for (pos in actualMinPos..actualMaxPos) {
            if (pos !in positionMap) {
                throw AssertionError("Gap at position $pos cannot be filled")
            }
        }

        // Build result in descending order
        val result = mutableListOf<Card>()
        for (pos in actualMaxPos downTo actualMinPos) {
            positionMap[pos]?.let { cardsAtPos ->
                result.addAll(cardsAtPos)
            }
        }

        return result
    }
}