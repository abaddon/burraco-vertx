package com.abaddon83.burraco.helpers

import com.abaddon83.burraco.game.events.game.*
import com.abaddon83.burraco.game.models.card.Card

data class GameDecksHelper(
    val playersCardsEvents: List<CardDealtWithPlayer>,
    val deckEvents :List<CardDealtWithDeck>,
    val playerDeck1Events:  List<CardDealtWithFirstPlayerDeck>,
    val playerDeck2Events: List<CardDealtWithSecondPlayerDeck>,
    val discardDeckEvents: List<CardDealtWithDiscardDeck>
) {

    fun events(): List<GameEvent> =deckEvents
        .plus(playerDeck2Events)
        .plus(playerDeck1Events)
        .plus(playersCardsEvents)
        .plus(discardDeckEvents)

    fun getCardFromDeck(position: Int): Card = deckEvents[position].card

    fun getCardsFromDiscardDeck(): List<Card> = discardDeckEvents.map { it.card }

}