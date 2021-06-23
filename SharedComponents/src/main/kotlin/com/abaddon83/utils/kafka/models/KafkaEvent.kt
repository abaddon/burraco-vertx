package com.abaddon83.utils.kafka.models

import com.abaddon83.burraco.common.serializations.InstantCustomSerializer
import com.abaddon83.burraco.common.serializations.UUIDCustomSerializer
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.*


@Serializable
open class KafkaEvent(
    val name: String,
    @Serializable(with = UUIDCustomSerializer::class)
    val entityKey: UUID,
    val entityName: String,
    @Serializable(with = InstantCustomSerializer::class)
    val instant: Instant,
    val jsonPayload: String
){
    override fun hashCode(): Int {
        return Objects.hash(name,entityKey,entityName,instant.toEpochMilli(),jsonPayload)
    }

    override fun equals(other: Any?): Boolean {
        return when(other){
            is KafkaEvent -> { hashCode() == other.hashCode()
//                val result = this.name == other.name &&
//                    this.entityKey ==other.entityKey &&
//                    this.entityName ==other.entityName &&
//                    this.instant.toEpochMilli() ==other.instant.toEpochMilli() &&
//                    this.jsonPayload ==other.jsonPayload
//                //1 == 1
//                result
            }
            else -> false
        }
    }
}



