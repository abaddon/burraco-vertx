package com.abaddon83.utils.kafka.models

import com.abaddon83.utils.serializations.InstantSerializer
import com.abaddon83.utils.serializations.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.*

@Serializable
open class KafkaEvent(
    val name: String,
    @Serializable(with = UUIDSerializer::class)
    val entityKey: UUID,
    val entityName: String,
    @Serializable(with = InstantSerializer::class)
    val instant: Instant,
    val jsonPayload: String
)



