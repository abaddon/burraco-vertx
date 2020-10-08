package com.abaddon83.vertx.burraco.engine.adapters.eventStoreVertx.model

import com.abaddon83.utils.es.Event
import com.fasterxml.jackson.annotation.JsonCreator
import io.vertx.core.json.JsonObject
import java.time.Instant
import java.util.*

data class ExtendEvent(
    val name: String, val entityKey: UUID, val entityName: String, val instant: Instant, val jsonPayload: String
) {
    @JsonCreator
    constructor(json: JsonObject) : this(json.mapTo(ExtendEvent::class.java))

    constructor(ev: ExtendEvent) : this(ev.name, ev.entityKey, ev.entityName, ev.instant, ev.jsonPayload)

    constructor(ev: Event) : this(
        ev::class.simpleName!!,
        UUID.fromString(ev.key()),
        "missing",
        Instant.now(),
        JsonObject.mapFrom(ev).encodePrettily()
    )

    fun toJson(): JsonObject {
        return JsonObject.mapFrom(this)
    }
}