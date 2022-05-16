//package com.abaddon83.burraco.game.adapters.eventBrokerProducerAdapter.config
//
//import kotlinx.serialization.encodeToString
//import kotlinx.serialization.json.Json
//
//
//data class KafkaGameProducerConfig(
//    val properties: Map<String, String>,
//    val topic: String
//){
//    fun producerConfig(): Map<String, String> = properties
//
//    fun topic(): String = topic;
//
//    fun toJson(): String{
//        return Json.encodeToString(this);
//    }
//
//}
//
////data class KafkaProducerConfig(
////    val bootstrapServer: String,
////    val keySerializer: String,
////    val valueSerializer: String,
////    val acks: String
////
////) {
////
////    constructor(): this(
////        bootstrapServer = "localhost:62666",
////        keySerializer = "org.apache.kafka.common.serialization.StringSerializer",
////        valueSerializer = "org.apache.kafka.common.serialization.StringSerializer",
////        acks = "-1"
////    )
////
////    fun producerConfig(): Map<String, String> = mapOf(
////        "bootstrap.servers"  = "localhost:62666",
////        "key.serializer" = "org.apache.kafka.common.serialization.StringSerializer"
////        "value.serializer" = "org.apache.kafka.common.serialization.StringSerializer"
////        "acks" = "-1"
////    )
////
////}
