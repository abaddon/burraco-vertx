package com.abaddon83.burraco.game.models.decks

import com.abaddon83.burraco.game.models.card.Card

data class Deck private constructor(override val cards: List<Card>) : IDeck {

    //override fun grabAllCards(): List<Card> = throw UnsupportedOperationException("You cannot grab all cards from the Deck")

    //override fun grabFirstCard(): Card = super.grabFirstCard()

    fun removeFirstCard(card: Card) : Deck {
        check(cards.first() == card){"Unexpected card found on top of the Deck!"}
        val updatedDeck = copy(cards= cards.subList(1,cards.size))
        check(updatedDeck.numCards() == numCards()-1)
        return updatedDeck
    }

    fun firstCard(): Card = cards.first()

    //fun shuffle(): Deck = Deck(cards.shuffled().toMutableList())

    companion object Factory {
        fun create(cards: List<Card>): Deck = Deck(cards.toMutableList())
//        fun create(): Deck {
//            val cards: List<Card> = listOf(
//                    ListCardsBuilder.allRanksWithJollyCards(),
//                    ListCardsBuilder.allRanksWithJollyCards()
//            ).flatten()
//
//            assert(cards.size == 108)
//            assert(cards.count {card -> card.rank == Ranks.Jolly && card.suit == Suits.Jolly} == 4){"The deck doesn't contain 4 ${Ranks.Jolly.label}"}
//            Suits.allSuit.forEach { suit ->
//                assert(cards.count { card -> card.suit == suit} == 26){"The card list doesn't contain 26 ${suit.icon} cards"}
//            }
//            Ranks.fullRanks.forEach{ rank ->
//                assert(cards.count {card -> card.rank == rank} == 8){"The card list doesn't contain 8 ${rank.label} cards"}
//            }
//
//            return Deck(cards.shuffled().toMutableList())
//        }
    }
}