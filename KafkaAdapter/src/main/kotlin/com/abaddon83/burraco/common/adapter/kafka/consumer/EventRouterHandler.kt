package com.abaddon83.burraco.common.adapter.kafka.consumer

import com.abaddon83.burraco.common.adapter.kafka.Handler
import com.abaddon83.burraco.common.externalEvents.KafkaEvent
import io.github.abaddon.kcqrs.core.helpers.KcqrsLoggerFactory.log
import io.vertx.kafka.client.consumer.KafkaConsumerRecord

class EventRouterHandler private constructor(
    private val eventsHandlers: Map<String, KafkaEventHandler>
) : Handler<KafkaConsumerRecord<String, String>> {

    constructor() : this(mapOf<String, KafkaEventHandler>())

    fun addHandler(handler: KafkaEventHandler): EventRouterHandler =
        EventRouterHandler(eventsHandlers.plus(handler.getEventName() to handler))

    override fun handle(event: KafkaConsumerRecord<String, String>?) {
        checkNotNull(event) { "received a record null" }
        val kafkaEvent = KafkaEvent.from(event.value())

        val handler = eventsHandlers[kafkaEvent.eventName]

        if (handler != null) {
            handler.handle(kafkaEvent)
        } else {
            handleMissingHandler(kafkaEvent)
        }
    }

    private fun handleMissingHandler(kafkaEvent: KafkaEvent) {
        log.warn("No handler registered for event: ${kafkaEvent.eventName}")
    }

}