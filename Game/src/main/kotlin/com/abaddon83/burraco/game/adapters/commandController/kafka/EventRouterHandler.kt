package com.abaddon83.burraco.game.adapters.commandController.kafka

import io.vertx.core.Handler
import io.vertx.kafka.client.consumer.KafkaConsumerRecord

class EventRouterHandler private constructor(
    private val eventsHandlers: Map<String, Handler<KafkaEvent>>
): Handler<KafkaConsumerRecord<String, String>> {

    constructor(): this(mapOf<String, Handler<KafkaEvent>>())

    fun addHandler(eventName: String, handler: Handler<KafkaEvent> ): EventRouterHandler=
        EventRouterHandler(eventsHandlers.plus(eventName to handler))

    override fun handle(record: KafkaConsumerRecord<String, String>?) {
        checkNotNull(record){"received a record null"}
        val kafkaEvent = KafkaEvent.from(record.value())
        val handler = eventsHandlers[kafkaEvent.eventName]
        checkNotNull(handler){"Handler for event ${kafkaEvent.eventName} not found"}
        handler.handle(kafkaEvent)
    }


}