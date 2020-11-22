package com.abaddon83.burraco.common.serializations.custom

import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import kotlinx.serialization.KSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

//object ListOfPlayerIdentitySerializer : KSerializer<List<PlayerIdentity>> {
//    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("listOfPlayerIdentity", PrimitiveKind.STRING)
//
//    override fun serialize(encoder: Encoder, value: List<PlayerIdentity>) {
//        val jsonElementList = value.map { playerIdentity ->
//            Json.encodeToJsonElement(PlayerIdentityCustomSerializer, playerIdentity)
//        }
//        val playerIdentityList = JsonArray(jsonElementList)
//        encoder.encodeString(playerIdentityList.toString())
//    }
//
//    override fun deserialize(decoder: Decoder): List<PlayerIdentity> {
//        return try {
//            val list = Json.decodeFromString<JsonArray>(decoder.toString())
//                .toList()
//                .map { jsonElement ->
//                    Json.decodeFromJsonElement(PlayerIdentityCustomSerializer, jsonElement)
//                }
//            list
//        } catch (ex: Exception) {
//            emptyList()
//        }
//    }
//}