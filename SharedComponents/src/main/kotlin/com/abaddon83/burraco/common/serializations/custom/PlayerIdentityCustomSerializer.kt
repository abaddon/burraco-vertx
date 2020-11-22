package com.abaddon83.burraco.common.serializations.custom

import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

//@Serializer(forClass = PlayerIdentity::class)
//class PlayerIdentityCustomSerializer: KSerializer<PlayerIdentity> {
//    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("PlayerIdentity", PrimitiveKind.STRING)
//
//    override fun serialize(encoder: Encoder, value: PlayerIdentity) {
//        val string = value.convertTo().toString()
//        encoder.encodeString(string)
//    }
//
//    override fun deserialize(decoder: Decoder): PlayerIdentity {
//        val uuidString = decoder.decodeString()
//        return PlayerIdentity.create(uuidString)!!
//    }
//}