package com.abaddon83.burraco.common.serializations

import com.abaddon83.burraco.common.models.valueObjects.Suits
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializer(forClass = Suits.Suit::class)
class SuitCustomSerializer : KSerializer<Suits.Suit> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Rank", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: Suits.Suit) {
        encoder.encodeString(value.label)
    }
    override fun deserialize(decoder: Decoder): Suits.Suit {
        return Suits.valueOf(decoder.decodeString())
    }
}