package com.abaddon83.burraco.common.models.entities

import com.abaddon83.burraco.common.models.identities.BurracoIdentity
import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.burraco.common.models.valueObjects.Ranks
import com.abaddon83.burraco.common.models.valueObjects.Suits
import kotlinx.serialization.Serializable
import kotlin.math.min

@Serializable
data class BurracoScale(
    override val identity: BurracoIdentity,
    val burracoCards: List<Card>
): Burraco() {
    private val suit: Suits.Suit;
    override val cards: List<Card>

    init {
        check(burracoCards.size >= 3) {"A Scale is composed by 3 or more cards"}
        val calculatedSuit = calculateScaleSuit(burracoCards)
        val validatedCards= validateSequence(burracoCards,calculatedSuit)
        this.suit=calculatedSuit
        this.cards = validatedCards
    }

    fun addCards(cardsToAdd: List<Card>): BurracoScale = this.copy(
        burracoCards = validateNewCards(cardsToAdd)
        )

    fun showSuit(): Suits.Suit = suit
    fun numCards():Int = cards.size

    private fun validateNewCards(cardsToValidate: List<Card>): List<Card> {
        check(cardsToValidate.find {c -> c.suit!= suit && c.suit!= Suits.Jolly } == null){warnMsg("The scale have multiple suites.")}
        return validateSequence(cards.plus(cardsToValidate), suit)
    }

    companion object Factory {
        fun create(cards: List<Card>): BurracoScale {
            return BurracoScale(identity = BurracoIdentity.create(), burracoCards = cards)
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

        private fun calculateScaleSuit(cards: List<Card>): Suits.Suit {
            val cardsBySuit = cards.groupBy { c -> c.suit }.mapValues { (_, v) -> v.size }
            check(cardsBySuit.keys.size <= 2) { "Too many different suits found: ${cardsBySuit.keys}" }
            val primarySuit = cardsBySuit.maxByOrNull { it.value }!!.key
            if (cardsBySuit.keys.size == 2) {
                val numCardsSecondarySuit = cardsBySuit.minByOrNull { it.value }!!.value
                assert(numCardsSecondarySuit == 1) { "Found $numCardsSecondarySuit with not a $primarySuit suit, max allowed is 1" }
            }
            return primarySuit
        }

        private fun appendRemainingJolly(cards: List<Card>, jollies: List<Card>): List<Card> {
            if (jollies.isEmpty()) return cards

            return when {
                cards.first().rank != Ranks.Ace -> jollies.plus(cards)
                cards.last().rank != Ranks.Ace -> cards.plus(jollies)
                else -> {
                    assert(false) { "there isn't a space to put a jolly in the Scale, the scalle is full" }
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
}