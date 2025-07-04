package com.abaddon83.burraco.game.models.decks

import com.abaddon83.burraco.common.models.card.Card


data class DiscardPile(override val cards: List<Card>) : IDeck {

    fun addCard(card: Card): DiscardPile {
        return copy(cards = cards.plus(card))
    }

    fun removeAllCards(cardsToRemove: List<Card>): DiscardPile {
        check(cards == cardsToRemove) { "Unexpected cards found on the DiscardPile!" }
        return copy(cards = listOf())
    }

    fun showCards(): List<Card> = cards.toList()

    companion object Factory {
        fun create(cards: List<Card>): DiscardPile {
            return DiscardPile(cards = cards)
        }
    }
}