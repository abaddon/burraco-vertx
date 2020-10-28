package com.abaddon83.burraco.common.models.valueObjects

import com.abaddon83.burraco.common.models.identities.BurracoIdentity
import com.abaddon83.burraco.common.models.identities.BurracoIdentityCustomSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializable(with = ScaleCustomSerializer::class)
open class Scale(
    override val identity: BurracoIdentity,
    val suit: Suits.Suit,
    override val cards: List<Card>): Burraco() {

    fun numCards():Int = cards.size
    fun showSuit(): Suits.Suit = suit

}

object ScaleCustomSerializer : KSerializer<Scale> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BurracoScale", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Scale) {
        val scaleString = Json.encodeToJsonElement(value.let { scale ->
            mapOf(
                "identity" to Json.encodeToJsonElement(BurracoIdentityCustomSerializer,scale.identity()),
                "cards" to JsonArray(scale.showCards().map { card ->
                    Json.encodeToJsonElement(CardCustomSerializer,card)
                }),
                "suit" to Json.encodeToJsonElement(scale.showSuit() .javaClass.simpleName),

                )
        })
        encoder.encodeString(scaleString.toString())
    }

    override fun deserialize(decoder: Decoder): Scale {
        val scaleJson = Json.decodeFromString<JsonElement>(decoder.decodeString())
        return scaleJson.let { scale ->
            val identity = Json.decodeFromJsonElement<BurracoIdentity>(BurracoIdentityCustomSerializer,scale.jsonObject.getValue("identity"))

            val cards = (scale.jsonObject.getValue("cards").jsonArray.map { jsonElement ->
                Json.decodeFromJsonElement<Card>(CardCustomSerializer,jsonElement)
            }).toList()

            val suit = Suits.valueOf(Json.decodeFromJsonElement<String>(scale.jsonObject.getValue("suit")))

            Scale(identity, suit, cards)
        }
    }
}