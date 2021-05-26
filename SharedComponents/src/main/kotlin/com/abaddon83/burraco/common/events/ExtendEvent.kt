package com.abaddon83.burraco.common.events

import com.abaddon83.burraco.common.serializations.custom.InstantCustomSerializer
import com.abaddon83.burraco.common.serializations.custom.UUIDCustomSerializer
import com.abaddon83.utils.ddd.Event
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import java.lang.Exception
import java.time.Instant
import java.util.*


@Serializable
data class ExtendEvent(
    val name: String, @Serializable(with = UUIDCustomSerializer::class) val entityKey: UUID, val entityName: String, @Serializable(with = InstantCustomSerializer::class) val instant: Instant, val jsonPayload: String
) {
    constructor(json: JsonObject) : this(
        name = json.getValue("name").toString(),// .getString("name"),
        entityKey = UUID.fromString(json.getValue("entityKey").toString()),
        entityName = json.getValue("entityName").toString(),
        instant = Instant.parse(json.getValue("instant").toString()),
        jsonPayload = json.getValue("jsonPayload").toString()
    )

    constructor(ev: ExtendEvent) : this(ev.name, ev.entityKey, ev.entityName, ev.instant, ev.jsonPayload)

    constructor(jsonString: String): this(Json.decodeFromString<ExtendEvent>(jsonString))

    constructor(ev: Event) : this(
        name = ev::class.simpleName!!,
        entityKey = UUID.fromString(ev.key()),
        entityName = ev.entityName,
        instant = ev.created,
        //JsonObject.mapFrom(ev).encodePrettily()
        jsonPayload = when (ev) {
            is BurracoGameCreated -> Json.encodeToString(ev)
            is PlayerAdded -> Json.encodeToString(ev)
            is GameInitialised -> Json.encodeToString(ev)

            is CardAssignedToPlayer -> Json.encodeToString(ev)
            is CardAssignedToPlayerDeck -> Json.encodeToString(ev)
            is CardAssignedToDeck -> Json.encodeToString(ev)
            is CardAssignedToDiscardDeck -> Json.encodeToString(ev)

            is GameStarted -> Json.encodeToString(ev)
            is CardPickedFromDeck -> Json.encodeToString(ev)
            is CardsPickedFromDiscardPile -> Json.encodeToString(ev)
            is CardDroppedIntoDiscardPile -> Json.encodeToString(ev)
            is TrisDropped -> Json.encodeToString(ev)
            is ScaleDropped -> Json.encodeToString(ev)
            is CardAddedOnBurraco -> Json.encodeToString(ev)
            is MazzettoPickedUp -> Json.encodeToString(ev)
            is TurnEnded -> Json.encodeToString(ev)
            else -> throw Exception("Event ${ev.javaClass.simpleName} not recognised")
        }
    )

    fun toJson(): JsonElement {
        return Json.encodeToJsonElement(this) //JsonObject.mapFrom(this)
    }

    fun toJsonString(): String {
        return Json.encodeToString(this)
    }

    fun toEvent(): Event = when (name) {
        "BurracoGameCreated" -> Json.decodeFromString<BurracoGameCreated>(jsonPayload)
        "PlayerAdded" -> Json.decodeFromString<PlayerAdded>(jsonPayload)
        "GameStarted" -> Json.decodeFromString<GameInitialised>(jsonPayload)

        "CardAssignedToPlayer" -> Json.decodeFromString<CardAssignedToPlayer>(jsonPayload)
        "CardAssignedToPlayerDeck" -> Json.decodeFromString<CardAssignedToPlayerDeck>(jsonPayload)
        "CardAssignedToDeck" -> Json.decodeFromString<CardAssignedToDeck>(jsonPayload)
        "CardAssignedToDiscardDeck" -> Json.decodeFromString<CardAssignedToDiscardDeck>(jsonPayload)

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