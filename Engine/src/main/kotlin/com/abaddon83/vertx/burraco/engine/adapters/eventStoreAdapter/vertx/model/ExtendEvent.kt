package com.abaddon83.vertx.burraco.engine.adapters.eventStoreAdapter.vertx.model

import com.abaddon83.utils.es.Event
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.vertx.core.json.JsonObject
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.time.Instant
import java.util.*

data class ExtendEvent(
    val name: String, val entityKey: UUID, val entityName: String, val instant: Instant, val jsonPayload: String
) {
    @JsonCreator
    constructor(json: JsonObject) : this(
        name = json.getString("name"),
        entityKey = UUID.fromString(json.getString("entityKey")),
        entityName = json.getString("entityName"),
        instant = json.getInstant("instant"),
        jsonPayload =json.getString("jsonPayload"))

    constructor(ev: ExtendEvent) : this(ev.name, ev.entityKey, ev.entityName, ev.instant, ev.jsonPayload)

    constructor(ev: Event) : this(
        ev::class.simpleName!!,
        UUID.fromString(ev.key()),
        ev.entityName,
        Instant.now(),
        JsonObject.mapFrom(ev).encodePrettily()
    )

    fun toJson(): JsonObject {
        return JsonObject.mapFrom(this)
    }

    fun toEvent(): Event{
        return Json.decodeFromString<Event>(jsonPayload)
    }
}