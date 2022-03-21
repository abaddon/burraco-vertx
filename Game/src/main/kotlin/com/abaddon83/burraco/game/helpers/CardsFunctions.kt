package com.abaddon83.burraco.game.helpers

import com.abaddon83.burraco.game.models.card.Card


fun <TCard : Card> Iterable<TCard>.removeCards(cardsToRemove: List<Card>): List<Card> =
    when(cardsToRemove.isNotEmpty()){
        true -> this
            .minus(cardsToRemove.first())
            .removeCards(cardsToRemove.drop(1))
        false -> this.toList()
    }
