package com.abaddon83.burraco.game.helpers

import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.card.Ranks


fun Iterable<Card>.removeCards(cardsToRemove: List<Card>): List<Card> =
    when(cardsToRemove.isNotEmpty()){
        true -> this
            .minus(cardsToRemove.first())
            .removeCards(cardsToRemove.drop(1))
        false -> this.toList()
    }

fun Iterable<Card>.containsJolly(): Boolean = this.map { it.rank }.contains(Ranks.Jolly)


fun Iterable<Card>.score(): Int = this.fold(0){ current, card -> current+card.score()}