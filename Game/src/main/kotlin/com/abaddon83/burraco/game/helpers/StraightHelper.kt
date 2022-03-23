package com.abaddon83.burraco.game.helpers

import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.card.Ranks
import com.abaddon83.burraco.game.models.card.Suits
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.min

object StraightHelper {
    val log: Logger = LoggerFactory.getLogger(this::class.simpleName)

    fun validStraight(cards: List<Card>): Boolean {
        if (cards.size < 3) {
            log.warn("A Straight is composed by 3 or more cards")
            return false
        }
        return when(val suit=calculateStraightSuit(cards)) {
            is Suits.Suit -> {
                try {
                    validateSequence(cards, suit)
                    true
                }catch (ex: Exception) {
                    log.warn(ex.message)
                    false
                }
            }
            else -> false
        }
    }

    private fun calculateStraightSuit(cards: List<Card>): Suits.Suit {
        val cardsBySuit = cards.groupBy { c -> c.suit }.mapValues { (_, v) -> v.size }
        check(cardsBySuit.keys.size <= 2) { "Too many different suits found: ${cardsBySuit.keys}" }
        val primarySuit = cardsBySuit.maxByOrNull { it.value }!!.key
        if (cardsBySuit.keys.size == 2) {
            val numCardsSecondarySuit = cardsBySuit.minByOrNull { it.value }!!.value
            assert(numCardsSecondarySuit == 1) { "Found $numCardsSecondarySuit with not a $primarySuit suit, max allowed is 1" }
        }
        return primarySuit
    }

    private fun validateSequence(cards: List<Card>, suit: Suits.Suit): List<Card> {
        val jollies = getJollies(cards, suit)
        val aces = cards.filter { c -> c.rank == Ranks.Ace }
        val sorted = (cards.minus(jollies).minus(aces)).sorted()
        val firstSort = (sorted.indices).flatMap { idx ->
            val currentPositionValue = sorted[idx].rank.position
            val nextPositionValue = sorted[min(idx + 1, sorted.size - 1)].rank.position
            when {
                currentPositionValue - 1 == nextPositionValue -> listOf(sorted[idx])
                currentPositionValue - 2 == nextPositionValue -> { assert(jollies.isNotEmpty()) { "Jolly missing" }
                    listOf(sorted[idx]).plus(jollies)
                }
                currentPositionValue == nextPositionValue -> listOf(sorted[idx])
                else -> { assert(false) { "The hole is too big.." }
                    listOf()
                }
            }
        }
        val sortedWithAce = appendAce(firstSort, aces, jollies.minus(firstSort))
        return appendRemainingJolly(sortedWithAce, jollies.minus(sortedWithAce))
    }

    private fun appendRemainingJolly(cards: List<Card>, jollies: List<Card>): List<Card> {
        if (jollies.isEmpty()) return cards

        return when {
            cards.first().rank != Ranks.Ace -> jollies.plus(cards)
            cards.last().rank != Ranks.Ace -> cards.plus(jollies)
            else -> {
                assert(false) { "there isn't a space to put a jolly in the Straight, the Straight is full" }
                listOf()
            }
        }
    }

    private fun appendAce(cards: List<Card>, aces: List<Card>, jollies: List<Card>): List<Card> {
        if (aces.isEmpty()) return cards

        return when {
            cards.first().rank == Ranks.King -> aces.plus(cards)
            cards.first().rank == Ranks.Queen && jollies.size == 1 -> aces.plus(jollies).plus(cards)
            cards.first().rank == Ranks.Queen && jollies.isEmpty() -> {
                assert(false) { "Jolly missing to cover the hole" }
                listOf()
            }
            cards.last().rank == Ranks.Two -> cards.plus(aces)
            cards.last().rank == Ranks.Three && jollies.isEmpty() -> {
                assert(false) { "Jolly missing to cover the hole" }
                listOf()
            }
            cards.last().rank == Ranks.Three && jollies.size == 1 -> cards.plus(jollies).plus(aces)
            cards.last().rank == Ranks.Three && jollies.isEmpty() -> {
                assert(false) { "Jolly missing to cover the hole" }
                listOf()
            }
            else -> {
                assert(false) { "boh" }; listOf()
            }
        }
    }

    private fun getJollies(cards: List<Card>, suit: Suits.Suit): List<Card> {
        val tmpJollies = cards.filter { c -> c.rank == Ranks.Jolly || c.rank == Ranks.Two }

        val jollies = when (tmpJollies.size) {
            //se la dimensione e' 2 significa che una delle 2 e' sicuramente un 2 valido
            2 -> tmpJollies.filterNot { c -> c.suit == suit && c.rank == Ranks.Two }
            else -> tmpJollies
        }

        assert(jollies.size <= 1) { "Too many jollies found: ${jollies}, max allowed 1" }
        return jollies
    }

}