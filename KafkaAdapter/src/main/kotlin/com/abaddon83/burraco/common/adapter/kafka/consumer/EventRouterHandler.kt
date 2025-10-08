package com.abaddon83.burraco.common.adapter.kafka.consumer

import com.abaddon83.burraco.common.adapter.kafka.Handler
import com.abaddon83.burraco.common.externalEvents.KafkaEvent
import io.vertx.kafka.client.consumer.KafkaConsumerRecord

class EventRouterHandler private constructor(
    private val eventsHandlers: Map<String, KafkaEventHandler>
) : Handler<KafkaConsumerRecord<String, String>> {

    constructor() : this(mapOf<String, KafkaEventHandler>())

    fun addHandler(eventName: String, handler: KafkaEventHandler): EventRouterHandler =
        EventRouterHandler(eventsHandlers.plus(eventName to handler))

    override fun handle(event: KafkaConsumerRecord<String, String>?) {
        checkNotNull(event) { "received a record null" }
        val kafkaEvent = KafkaEvent.from(event.value())
        val handler = eventsHandlers[kafkaEvent.eventName]
        checkNotNull(handler) { "Handler for event ${kafkaEvent.eventName} not found" }

        handler.handle(kafkaEvent)
    }

}