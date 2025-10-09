package com.abaddon83.burraco.game.adapters.commandController.kafka

import com.abaddon83.burraco.common.externalEvents.KafkaEvent
import com.abaddon83.burraco.common.adapter.kafka.consumer.EventRouterHandler
import com.abaddon83.burraco.common.adapter.kafka.consumer.KafkaEventHandler
import com.abaddon83.burraco.common.helpers.Validated
import io.vertx.core.json.Json
import io.vertx.junit5.VertxExtension
import io.vertx.kafka.client.consumer.KafkaConsumerRecord
import io.vertx.kafka.client.consumer.impl.KafkaConsumerRecordImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
internal class EventRouterHandlerTest {

    @Test
    fun `Given an EventRouterHandler with the right handler when receive a record, it's managed`() {
        val eventName = "event1"
        val dummyEventHandler = DummyKafkaEventHandler()

        val eventRouterHandler = EventRouterHandler()
            .addHandler(eventName, dummyEventHandler) //{ counter += 1 }

        val kafkaEvent = KafkaEvent(eventName, "test")
        val record: KafkaConsumerRecord<String, String> =
            KafkaConsumerRecordImpl(ConsumerRecord("topic", 1, 0, "key", Json.encode(kafkaEvent)))

        assertDoesNotThrow() {
            eventRouterHandler.handle(record)
        }
    }

    internal class DummyKafkaEventHandler() : KafkaEventHandler("event1") {

        override fun getCoroutineIOScope(): CoroutineScope {
            return CoroutineScope(Dispatchers.IO + SupervisorJob())
        }

        override suspend fun handleKafkaEventRequest(event: KafkaEvent): Validated<*, *> {
            return Validated.Valid(Unit)
        }

    }
}