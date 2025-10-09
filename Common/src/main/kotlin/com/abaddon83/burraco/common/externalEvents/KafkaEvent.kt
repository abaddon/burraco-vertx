package com.abaddon83.burraco.common.externalEvents

import com.fasterxml.jackson.annotation.JsonProperty
import io.vertx.core.json.Json

class KafkaEvent(
    @JsonProperty("eventName")
    val eventName: String,
    @JsonProperty("eventPayload")
    val eventPayload: String
) {
    fun toJson(): String = Json.encode(this);

    companion object {
        fun <E1 : ExternalEvent> from(externalEvent: E1): KafkaEvent {
            return KafkaEvent(externalEvent.eventName, Json.encode(externalEvent))
        }


        fun from(json: String): KafkaEvent =
            Json.decodeValue(json, KafkaEvent::class.java)
    }
}