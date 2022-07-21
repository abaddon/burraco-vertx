//package com.abaddon83.burraco.dealer.adapters.gameEventCommandAdapter.KafkaGameConsumerAdapter
//
//import com.fasterxml.jackson.annotation.JsonProperty
//import io.vertx.core.json.Json
//import org.apache.kafka.clients.consumer.ConsumerRecord
//
//data class KafkaEvent private constructor(
//    @JsonProperty("eventName")
//    val eventName: String,
//    @JsonProperty("eventOwner")
//    val eventOwner: String,
//    @JsonProperty("eventPayload")
//    val eventPayload: String
//) {
//
//    fun toJsonString(): String {
//        return Json.encode(this)
//    }
//
//    companion object{
//        fun createFrom(record: ConsumerRecord<String, String>): KafkaEvent =
//            Json.decodeValue(record.value(), KafkaEvent::class.java)
//
//    }
////    constructor(event: IDomainEvent): this(
////        eventName = event::class.simpleName!!,
////        key = event.aggregateId.valueAsString(),
////        entityName = event.aggregateType,
////        instant = event.header.standardValue(HeadersType.WHEN).toLong(),
////        jsonPayload = when(event){
////            is CardDealtToDeck -> Json.encodePrettily(event)
////            is CardDealtToDiscardDeck -> Json.encodePrettily(event)
////            is CardDealtToPlayer -> Json.encodePrettily(event)
////            is CardDealtToPlayerDeck1 -> Json.encodePrettily(event)
////            is CardDealtToPlayerDeck2 -> Json.encodePrettily(event)
////            is DeckCreated -> Json.encodePrettily(event)
////            else -> throw Exception("Event ${event::class.simpleName} not recognised")
////        }
////
////    )
//
//}
