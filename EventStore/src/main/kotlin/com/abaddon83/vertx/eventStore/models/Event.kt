package com.abaddon83.vertx.eventStore.models

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
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
)

@Serializer(forClass = UUID::class)
class UUIDSerializer: KSerializer<UUID> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("GameIdentity", PrimitiveKind.STRING)

    override fun serialize(output: Encoder, obj: UUID) {
        output.encodeString(obj.toString())
    }

    override fun deserialize(input: Decoder): UUID {
        return UUID.fromString(input.decodeString())
    }
}

@Serializer(forClass = Instant::class)
class InstantSerializer: KSerializer<Instant> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    override fun serialize(output: Encoder, obj: Instant) {
        output.encodeString(obj.toEpochMilli().toString())
    }

    override fun deserialize(input: Decoder): Instant {
        return Instant.ofEpochMilli(input.decodeLong())
    }
}