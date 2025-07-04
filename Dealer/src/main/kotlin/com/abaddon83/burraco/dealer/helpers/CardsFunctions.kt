package com.abaddon83.burraco.dealer.helpers

import com.abaddon83.burraco.common.models.card.Card


fun Iterable<Card>.removeCards(cardsToRemove: List<Card>): List<Card> =
    when(cardsToRemove.isNotEmpty()){
        true -> this
            .minus(cardsToRemove.first())
            .removeCards(cardsToRemove.drop(1))
        false -> this.toList()
    }