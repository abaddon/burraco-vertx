package com.abaddon83.vertx.eventStore.models

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.time.Instant
import java.util.*

open class Event(
    val name: String,
    val entityKey: UUID,
    val entityName: String,
    val instant: Instant,
    val jsonPayload: String
)