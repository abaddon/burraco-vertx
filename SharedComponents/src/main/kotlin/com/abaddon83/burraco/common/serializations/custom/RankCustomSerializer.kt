package com.abaddon83.burraco.common.serializations.custom

import com.abaddon83.burraco.common.models.valueObjects.Ranks
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializer(forClass = Ranks.Rank::class)
class RankCustomSerializer : KSerializer<Ranks.Rank> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Rank", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: Ranks.Rank) {
        encoder.encodeString(value.javaClass.simpleName)
    }
    override fun deserialize(decoder: Decoder): Ranks.Rank {
        return Ranks.valueOf(decoder.decodeString())
    }
}