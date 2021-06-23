package com.abaddon83.burraco.common.serializations.custom

//@Serializer(forClass = Tris::class)
//class TrisCustomSerializer : KSerializer<Tris> {
//    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Tris", PrimitiveKind.STRING)
//
//    override fun serialize(encoder: Encoder, value: Tris) {
//        val trisString = Json.encodeToJsonElement(value.let { tris ->
//            mapOf(
//                "identity" to Json.encodeToJsonElement(tris.identity()),
//                "cards" to JsonArray(tris.showCards().map { card ->
//                    Json.encodeToJsonElement(card)
//                }),
//                "rank" to Json.encodeToJsonElement(tris.showRank().javaClass.simpleName),
//
//                )
//        })
//        encoder.encodeString(trisString.toString())
//    }
//
//    override fun deserialize(decoder: Decoder): Tris {
//        val trisJson = Json.decodeFromString<JsonElement>(decoder.decodeString())
//        return trisJson.let { tris ->
//            val identity = Json.decodeFromJsonElement<BurracoIdentity>(tris.jsonObject.getValue("identity"))
//
//            val cards = (tris.jsonObject.getValue("cards").jsonArray.map { jsonElement ->
//                Json.decodeFromJsonElement<Card>(jsonElement)
//            }).toList()
//
//            val rank = Ranks.valueOf(Json.decodeFromJsonElement<String>(tris.jsonObject.getValue("rank")))
//
//            Tris(identity, rank, cards)
//        }
//    }
//}