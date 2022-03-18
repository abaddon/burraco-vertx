package com.abaddon83.burraco.game.models.decks

import com.abaddon83.burraco.game.models.card.Card


interface IDeck{
    val cards: List<Card>

    fun numCards(): Int = cards.size

    //fun grabFirstCard(): Card = cards.removeAt(0)

//    fun grabAllCards(): List<Card> {
//        val grabbedCards = cards.toList()
//        cards.removeAll(grabbedCards)
//        assert(cards.size == 0)
//        return grabbedCards
//    }

}