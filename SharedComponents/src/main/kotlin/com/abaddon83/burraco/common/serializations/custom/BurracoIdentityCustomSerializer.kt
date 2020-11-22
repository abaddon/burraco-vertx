package com.abaddon83.burraco.common.serializations.custom

import com.abaddon83.burraco.common.models.identities.BurracoIdentity
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

//@Serializer(forClass = BurracoIdentity::class)
//class BurracoIdentityCustomSerializer: KSerializer<BurracoIdentity> {
//    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BurracoIdentity", PrimitiveKind.STRING)
//
//    override fun serialize(encoder: Encoder, value: BurracoIdentity) {
//        val string = value.convertTo().toString()
//        encoder.encodeString(string)
//    }
//
//    override fun deserialize(decoder: Decoder): BurracoIdentity {
//        val uuidString = decoder.decodeString()
//        return BurracoIdentity.create(uuidString)
//    }
//}