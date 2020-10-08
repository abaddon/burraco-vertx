package com.abaddon83.vertx.burraco.engine.adapters.commandController.models

import com.abaddon83.vertx.burraco.engine.models.decks.Card
import com.abaddon83.vertx.burraco.engine.models.decks.Ranks
import com.abaddon83.vertx.burraco.engine.models.decks.Suits

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