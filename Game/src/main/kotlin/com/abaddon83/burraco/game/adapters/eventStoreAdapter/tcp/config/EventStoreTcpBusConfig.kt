//package com.abaddon83.burraco.game.adapters.eventStoreAdapter.tcp.config
//
//import kotlinx.serialization.json.JsonObject
//import kotlinx.serialization.json.buildJsonObject
//import kotlinx.serialization.json.put
//import kotlinx.serialization.json.putJsonObject
//
//data class EventStoreTcpBusConfig(
//    val service: ServiceConfig,
//    val channels: ChannelsConfig
//) {
//    fun toJson(): JsonObject =
//        buildJsonObject {
//            putJsonObject("service") {
//                put("address", service.address)
//                put("port", service.port)
//            }
//            putJsonObject("channels") {
//                put("publish", channels.publish)
//                put("query", channels.query)
//            }
//        }
//}
//
//data class ServiceConfig(
//    val address: String,
//    val port: Int
//)
//
//data class ChannelsConfig(
//    val publish: String,
//    val query: String
//)
