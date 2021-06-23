package com.abaddon83.eventStore.models



import com.abaddon83.burraco.common.serializations.InstantCustomSerializer
import com.abaddon83.burraco.common.serializations.UUIDCustomSerializer
import io.vertx.core.json.JsonObject
import kotlinx.serialization.Serializable

import java.time.Instant
import java.util.*

@Serializable
open class Event(
    val name: String,
    @Serializable(with = UUIDCustomSerializer::class)
    val entityKey: UUID,
    val entityName: String,
    @Serializable(with = InstantCustomSerializer::class)
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