package com.abaddon83.burraco.game.models.decks

import com.abaddon83.burraco.game.models.card.Card

data class Deck private constructor(override val cards: List<Card>) : IDeck {

    fun removeFirstCard(card: Card) : Deck {
        check(cards.first() == card){"Unexpected card found on top of the Deck!"}
        val updatedDeck = copy(cards= cards.subList(1,cards.size))
        check(updatedDeck.numCards() == numCards()-1)
        return updatedDeck
    }

    fun firstCard(): Card = cards.first()

    companion object Factory {
        fun create(cards: List<Card>): Deck = Deck(cards.toMutableList())
    }
}