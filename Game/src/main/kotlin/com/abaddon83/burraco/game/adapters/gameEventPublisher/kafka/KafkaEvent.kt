package com.abaddon83.burraco.game.adapters.gameEventPublisher.kafka

import com.abaddon83.burraco.game.events.game.CardDealingRequested
import com.abaddon83.burraco.game.events.game.GameEvent
import io.vertx.core.json.Json

data class KafkaEvent(
    val eventName: String,
    val eventOwner: String,
    val eventPayload: String
) {

    fun toJson(): String = Json.encode(this);

    companion object{
        fun from(gameEvet: GameEvent): KafkaEvent? =
            when(gameEvet){
                is CardDealingRequested -> KafkaEvent(gameEvet.javaClass.simpleName,"GameService", gameEvet.toJson())
                else -> null
            }
        }
}