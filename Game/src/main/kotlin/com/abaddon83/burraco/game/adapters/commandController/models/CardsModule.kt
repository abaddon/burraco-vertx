package com.abaddon83.burraco.game.adapters.commandController.models

import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.burraco.common.models.valueObjects.Ranks
import com.abaddon83.burraco.common.models.valueObjects.Suits

data class CardsModule(
        val cards: List<CardModule>
) {

    fun to(): List<Card> =
            cards.map {
                Card(suit = Suits.valueOf(it.suit.name), rank = Ranks.valueOf(it.rank.name))
            }

    companion object Factory {
        fun from(cards: List<Card>): CardsModule =
                CardsModule(
                        cards.map { CardModule.from(it) }
                )
    }
}