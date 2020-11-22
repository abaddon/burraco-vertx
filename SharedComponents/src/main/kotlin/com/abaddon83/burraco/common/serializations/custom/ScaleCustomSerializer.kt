package com.abaddon83.burraco.common.serializations.custom

import com.abaddon83.burraco.common.models.identities.BurracoIdentity
import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.burraco.common.models.valueObjects.Ranks
import com.abaddon83.burraco.common.models.valueObjects.Scale
import com.abaddon83.burraco.common.models.valueObjects.Suits
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

//@Serializer(forClass = Scale::class)
//class ScaleCustomSerializer : KSerializer<Scale> {
//    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BurracoScale", PrimitiveKind.STRING)
//
//    override fun serialize(encoder: Encoder, value: Scale) {
//        val scaleString = Json.encodeToJsonElement(value.let { scale ->
//            mapOf(
//                "identity" to Json.encodeToJsonElement(scale.identity()),
//                "cards" to JsonArray(scale.showCards().map { card ->
//                    Json.encodeToJsonElement(card)
//                }),
//                "suit" to Json.encodeToJsonElement(scale.showSuit() .javaClass.simpleName),
//
//                )
//        })
//        encoder.encodeString(scaleString.toString())
//    }
//
//    override fun deserialize(decoder: Decoder): Scale {
//        val scaleJson = Json.decodeFromString<JsonElement>(decoder.decodeString())
//        return scaleJson.let { scale ->
//            val identity = Json.decodeFromJsonElement<BurracoIdentity>(scale.jsonObject.getValue("identity"))
//
//            val cards = (scale.jsonObject.getValue("cards").jsonArray.map { jsonElement ->
//                Json.decodeFromJsonElement<Card>(jsonElement)
//            }).toList()
//
//            val suit = Suits.valueOf(Json.decodeFromJsonElement<String>(scale.jsonObject.getValue("suit")))
//
//            Scale(identity, suit, cards)
//        }
//    }
//}