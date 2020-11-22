package com.abaddon83.burraco.common.serializations.custom

import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.valueObjects.Card
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

//@Serializer(forClass = GameIdentity::class)
//class GameIdentityCustomSerializer: KSerializer<GameIdentity> {
//    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("GameIdentity", PrimitiveKind.STRING)
//
//    override fun serialize(encoder: Encoder, value: GameIdentity) {
//        val string = value.convertTo().toString()
//        encoder.encodeString(string)
//    }
//
//    override fun deserialize(decoder: Decoder): GameIdentity {
//        val uuidString = decoder.decodeString()
//        return GameIdentity.create(uuidString)!!
//    }
//}