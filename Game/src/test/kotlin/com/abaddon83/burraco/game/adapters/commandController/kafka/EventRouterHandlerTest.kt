package com.abaddon83.burraco.game.adapters.commandController.kafka

import io.vertx.core.Handler
import io.vertx.core.json.Json
import io.vertx.kafka.client.consumer.KafkaConsumerRecord
import io.vertx.kafka.client.consumer.impl.KafkaConsumerRecordImpl
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class EventRouterHandlerTest{

    @Test
    fun `Given an EventRouterHandler empty when receive a record exception`(){
        val eventRouterHandler= EventRouterHandler()
        val kafkaEvent=KafkaEvent("event1","test","")
        val record: KafkaConsumerRecord<String, String> = KafkaConsumerRecordImpl(ConsumerRecord("topic",1,0,"key",Json.encode(kafkaEvent)))

        val exception=assertThrows(IllegalStateException::class.java) {
            eventRouterHandler.handle(record)
        }
        assertEquals("Handler for event event1 not found",exception.message)
    }

    @Test
    fun `Given an EventRouterHandler with the right handler when receive a record, it's managed`(){
        val eventName="event1"
        var counter = 0
        val eventRouterHandler= EventRouterHandler()
            .addHandler(eventName, Handler { counter += 1 })

        val kafkaEvent=KafkaEvent(eventName,"test","")
        val record: KafkaConsumerRecord<String, String> = KafkaConsumerRecordImpl(ConsumerRecord("topic",1,0,"key",Json.encode(kafkaEvent)))

        assertDoesNotThrow(){
            eventRouterHandler.handle(record)
        }
        assertEquals(1,counter)
    }

}