//package com.abaddon83.burraco.dealer.adapters.eventAdapters.kafka
//
//import com.abaddon83.burraco.dealer.events.*
//import io.github.abaddon.kcqrs.core.domain.messages.HeadersType
//import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
//import io.vertx.core.json.Json
//
//data class KafkaEvent(
//    val eventName: String,
//    val key: String,
//    val entityName: String,
//    val instant: Long,
//    val jsonPayload: String
//) {
//
//    fun toJsonString(): String {
//        return Json.encode(this)
//    }
//
//    constructor(event: IDomainEvent): this(
//        eventName = event::class.simpleName!!,
//        key = event.aggregateId.valueAsString(),
//        entityName = event.aggregateType,
//        instant = event.header.standardValue(HeadersType.WHEN).toLong(),
//        jsonPayload = when(event){
//            is CardDealtToDeck -> Json.encodePrettily(event)
//            is CardDealtToDiscardDeck -> Json.encodePrettily(event)
//            is CardDealtToPlayer -> Json.encodePrettily(event)
//            is CardDealtToPlayerDeck1 -> Json.encodePrettily(event)
//            is CardDealtToPlayerDeck2 -> Json.encodePrettily(event)
//            is DeckCreated -> Json.encodePrettily(event)
//            else -> throw Exception("Event ${event::class.simpleName} not recognised")
//        }
//
//    )
//
//}
