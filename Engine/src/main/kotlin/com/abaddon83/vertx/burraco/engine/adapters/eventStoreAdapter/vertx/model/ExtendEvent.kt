package com.abaddon83.vertx.burraco.engine.adapters.eventStoreAdapter.vertx.model

import com.abaddon83.utils.es.Event
import com.abaddon83.burraco.common.events.*
import com.fasterxml.jackson.annotation.JsonCreator
import io.vertx.core.json.JsonObject
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.lang.Exception
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
        jsonPayload = json.getString("jsonPayload")
    )

    constructor(ev: ExtendEvent) : this(ev.name, ev.entityKey, ev.entityName, ev.instant, ev.jsonPayload)

    constructor(ev: Event) : this(
        name = ev::class.simpleName!!,
        entityKey = UUID.fromString(ev.key()),
        entityName = ev.entityName,
        instant = ev.created,
        //JsonObject.mapFrom(ev).encodePrettily()
        jsonPayload = when (ev) {
            is BurracoGameCreated -> Json.encodeToString(ev)
            is PlayerAdded -> Json.encodeToString(ev)
            is GameStarted -> Json.encodeToString(ev)
            is CardPickedFromDeck -> Json.encodeToString(ev)
            is CardsPickedFromDiscardPile -> Json.encodeToString(ev)
            is CardDroppedIntoDiscardPile -> Json.encodeToString(ev)
            is TrisDropped -> Json.encodeToString(ev)
            is ScaleDropped -> Json.encodeToString(ev)
            is CardAddedOnBurraco -> Json.encodeToString(ev)
            is MazzettoPickedUp -> Json.encodeToString(ev)
            is TurnEnded -> Json.encodeToString(ev)
            else -> throw Exception("Event not recognised")
        }
    )

    fun toJson(): JsonObject {
        return JsonObject.mapFrom(this)
    }

    fun toEvent(): Event = when (name) {
        "BurracoGameCreated" -> Json.decodeFromString<BurracoGameCreated>(jsonPayload)
        "PlayerAdded" -> Json.decodeFromString<PlayerAdded>(jsonPayload)
        "GameStarted" -> Json.decodeFromString<GameStarted>(jsonPayload)
        "CardPickedFromDeck" -> Json.decodeFromString<CardPickedFromDeck>(jsonPayload)
        "CardsPickedFromDiscardPile" -> Json.decodeFromString<CardsPickedFromDiscardPile>(jsonPayload)
        "CardDroppedIntoDiscardPile" -> Json.decodeFromString<CardDroppedIntoDiscardPile>(jsonPayload)
        "ScaleDropped" -> Json.decodeFromString<ScaleDropped>(jsonPayload)
        "CardAddedOnBurraco" -> Json.decodeFromString<CardAddedOnBurraco>(jsonPayload)
        "MazzettoPickedUp" -> Json.decodeFromString<MazzettoPickedUp>(jsonPayload)
        "TurnEnded" -> Json.decodeFromString<TurnEnded>(jsonPayload)
        else -> throw Exception("class missing")
    }

}