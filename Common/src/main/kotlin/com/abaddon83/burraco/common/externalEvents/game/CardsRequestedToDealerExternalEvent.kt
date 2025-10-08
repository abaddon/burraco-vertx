package com.abaddon83.burraco.common.externalEvents.game

import com.abaddon83.burraco.common.externalEvents.KafkaEvent
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.fasterxml.jackson.annotation.JsonProperty
import io.vertx.core.json.Json

data class CardsRequestedToDealerExternalEvent(
    @JsonProperty("aggregateIdentity")
    override val aggregateIdentity: GameIdentity,
    @JsonProperty("players")
    val players: List<PlayerIdentity>
) : GameExternalEvent(aggregateIdentity, GameEventName.CardsRequestedToDealer) {

//    override fun toKafkaEvent(): KafkaEvent {
//        return KafkaEvent(eventName, Json.encode(this))
//    }
}
