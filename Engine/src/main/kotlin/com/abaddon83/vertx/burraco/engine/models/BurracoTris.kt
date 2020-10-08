package com.abaddon83.vertx.burraco.engine.models

import com.abaddon83.vertx.burraco.engine.models.burracos.BurracoIdentity
import com.abaddon83.vertx.burraco.engine.models.burracos.BurracoIdentityCustomSerializer
import com.abaddon83.vertx.burraco.engine.models.burracos.Tris
import com.abaddon83.vertx.burraco.engine.models.decks.Card
import com.abaddon83.vertx.burraco.engine.models.decks.CardCustomSerializer
import com.abaddon83.vertx.burraco.engine.models.decks.Ranks
import com.abaddon83.vertx.burraco.engine.models.games.GameIdentity
import com.abaddon83.vertx.burraco.engine.models.games.GameIdentityCustomSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializable(with = BurracoTrisCustomSerializer::class)
data class BurracoTris constructor(
        override val identity: BurracoIdentity,
        override val rank: Ranks.Rank,
        override val cards: List<Card>
) : Tris("BurracoTris") {

    override fun addCards(cardsToAdd: List<Card>): BurracoTris {
        return copy(cards = validateNewCards(cardsToAdd))
    }

    override fun validateNewCards(cardsToValidate: List<Card>): List<Card> {
        val tmpCardList = cardsToValidate.plus(this.cards)
        val cardsWithoutJolly = tmpCardList.filterNot { c -> c.rank == Ranks.Jolly || c.rank == Ranks.Two }
        check((tmpCardList.minus(cardsWithoutJolly)).size <= 1) { warnMsg("A tris can contain at least 1 Jolly or Two") }
        check(cardsWithoutJolly.filterNot { c -> c.rank == rank }.isEmpty()) { warnMsg("A tris is composed by cards with the same rank") }
        return tmpCardList
    }

    companion object Factory {
        fun create(cards: List<Card>): BurracoTris {
            check(cards.size >= 3) { "A tris is composed by 3 or more cards" }
            val rank = calculateTrisRank(cards)
            check(!listOf(Ranks.Two, Ranks.Jolly).contains(rank)) { "Tris of Jolly or Two are not allowed" }
            return BurracoTris(identity = BurracoIdentity.create(), rank = rank, cards = cards)
        }

        private fun calculateTrisRank(cards: List<Card>): Ranks.Rank {
            val cardsByRank = cards.groupBy { c -> c.rank }.mapValues { (k, v) -> v.size }

            val cardsByRankWithoutJollyAndTwo = cardsByRank.minus(Ranks.Jolly).minus(Ranks.Two)

            assert(cardsByRankWithoutJollyAndTwo.keys.size == 1) { "Too many different ranks found: ${cardsByRank.keys}" }
            return checkNotNull(cardsByRank.maxBy { it.value }?.key) { "Tris Rank calculation failed" }
        }

    }

}


object BurracoTrisCustomSerializer : KSerializer<BurracoTris> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BurracoTris", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: BurracoTris) {
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

    override fun deserialize(decoder: Decoder): BurracoTris {
        val trisJson = Json.decodeFromString<JsonElement>(decoder.decodeString())
        return trisJson.let { tris ->
            val identity = Json.decodeFromJsonElement<BurracoIdentity>(BurracoIdentityCustomSerializer,tris.jsonObject.getValue("identity"))

            val cards = (tris.jsonObject.getValue("cards").jsonArray.map { jsonElement ->
                Json.decodeFromJsonElement<Card>(CardCustomSerializer,jsonElement)
            }).toList()

            val rank = Ranks.valueOf(Json.decodeFromJsonElement<String>(tris.jsonObject.getValue("rank")))

            BurracoTris(identity, rank, cards)
        }
    }
}