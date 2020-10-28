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

@Serializable(with = TrisCustomSerializer::class)
open class Tris(override val identity: BurracoIdentity,
                val rank: Ranks.Rank,
                override val cards: List<Card>

) : Burraco(){

    fun showRank(): Ranks.Rank = rank
    fun numCards():Int = cards.size
}


object TrisCustomSerializer : KSerializer<Tris> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Tris", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Tris) {
        val trisString = Json.encodeToJsonElement(value.let { tris ->
            mapOf(
                "identity" to Json.encodeToJsonElement(BurracoIdentityCustomSerializer,tris.identity()),
                "cards" to JsonArray(tris.showCards().map { card ->
                    Json.encodeToJsonElement(CardCustomSerializer,card)
                }),
                "rank" to Json.encodeToJsonElement(tris.showRank().javaClass.simpleName),

                )
        })
        encoder.encodeString(trisString.toString())
    }

    override fun deserialize(decoder: Decoder): Tris {
        val trisJson = Json.decodeFromString<JsonElement>(decoder.decodeString())
        return trisJson.let { tris ->
            val identity = Json.decodeFromJsonElement<BurracoIdentity>(BurracoIdentityCustomSerializer,tris.jsonObject.getValue("identity"))

            val cards = (tris.jsonObject.getValue("cards").jsonArray.map { jsonElement ->
                Json.decodeFromJsonElement<Card>(CardCustomSerializer,jsonElement)
            }).toList()

            val rank = Ranks.valueOf(Json.decodeFromJsonElement<String>(tris.jsonObject.getValue("rank")))

            Tris(identity, rank, cards)
        }
    }
}