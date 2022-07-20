package com.abaddon83.burraco.game.adapters.commandController.kafka

import com.fasterxml.jackson.annotation.JsonProperty
import io.vertx.core.json.Json

data class KafkaEvent constructor(
    @JsonProperty("eventName")
    val eventName: String,
    @JsonProperty("eventOwner")
    val eventOwner: String,
    @JsonProperty("eventPayload")
    val eventPayload: String
){
    companion object{
        fun from(json: String): KafkaEvent =
            Json.decodeValue(json,KafkaEvent::class.java)
    }
}