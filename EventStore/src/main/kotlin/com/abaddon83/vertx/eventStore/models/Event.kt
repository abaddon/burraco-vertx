package com.abaddon83.vertx.eventStore.models

import com.abaddon83.utils.serializations.InstantSerializer
import com.abaddon83.utils.serializations.UUIDSerializer
import io.vertx.core.json.JsonObject
import kotlinx.serialization.Serializable

import java.time.Instant
import java.util.*

@Serializable
open class Event(
    val name: String,
    @Serializable(with = UUIDSerializer::class)
    val entityKey: UUID,
    val entityName: String,
    @Serializable(with = InstantSerializer::class)
    val instant: Instant,
    val jsonPayload: String
){
    constructor(jsonObject: JsonObject): this(
        name = jsonObject.getString("name"),
        entityKey = UUID.fromString(jsonObject.getString("entityKey")),
        entityName = jsonObject.getString("entityName"),
        instant = jsonObject.getInstant("instant"),
        jsonPayload = jsonObject.getString("jsonPayload")
    )
}