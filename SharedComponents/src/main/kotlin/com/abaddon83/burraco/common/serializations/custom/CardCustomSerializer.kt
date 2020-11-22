package com.abaddon83.burraco.common.serializations.custom

import com.abaddon83.burraco.common.models.identities.BurracoIdentity
import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.burraco.common.models.valueObjects.Ranks
import com.abaddon83.burraco.common.models.valueObjects.Suits
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

//@Serializer(forClass = Card::class)
//class CardCustomSerializer : KSerializer<Card> {
//    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Card", PrimitiveKind.STRING)
//
//    override fun serialize(encoder: Encoder, value: Card) {
//        val cardString = Json.encodeToJsonElement(value.let { card ->
//            mapOf( "rank" to card.rank.javaClass.simpleName, "suit" to card.suit.javaClass.simpleName)
//        })
//        encoder.encodeString(cardString.toString())
//    }
//
//    override fun deserialize(decoder: Decoder): Card {
//
////        val cardJson = Json.decodeFromString<JsonElement>(decoder.decodeString())
////        val cardMap = Json.decodeFromJsonElement<Map<String,String>>(cardJson)
//
//        val cardMap = Json.decodeFromString<Map<String,String>>(decoder.decodeString())
//
//        return Card(
//            suit = Suits.valueOf(cardMap.getValue("suit")),
//            rank = Ranks.valueOf(cardMap.getValue("rank"))
//        )
//    }
//}