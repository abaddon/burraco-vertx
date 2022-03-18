package com.abaddon83.burraco.game.models.decks

import com.abaddon83.burraco.game.models.card.Card


data class DiscardPile private constructor(override val cards: List<Card>): IDeck {

//    override fun grabAllCards(): List<Card> {
//        check(cards.size >0){"The DiscardPile is empty, you can't grab a card from here"}
//        return super.grabAllCards()
//    }
//
//    override fun grabFirstCard(): Card = throw UnsupportedOperationException("You cannot grab only one card from the DiscardPile")


    fun addCard(card: Card): DiscardPile {
        return copy(cards = cards.plus(card))
    }

    fun removeAllCards(cardsToRemove: List<Card>): DiscardPile{
        check(cards == cardsToRemove){"Unexpected cards found on the DiscardPile!"}
        val updatedCards = cards.minus(cardsToRemove)
        check(updatedCards.isEmpty()){"The new discard pile should be empty"}
        return copy(cards = updatedCards)
    }

    fun showCards(): List<Card> = cards.toList()

    companion object Factory {
        fun create(cards: List<Card>): DiscardPile {
            return DiscardPile(cards = cards.toMutableList())
        }
    }
}