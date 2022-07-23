package com.abaddon83.burraco.dealer

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ServiceConfigTest{

    @Test
    fun `test`() {
        val serviceConfig =
            ServiceConfig.load("src/test/resources/dev.yml")

        assertEquals("game_event", serviceConfig.gameEventConsumer.topic())
        assertEquals("localhost:9093", serviceConfig.gameEventConsumer.consumerConfig()["bootstrap.servers"])
        assertEquals("org.apache.kafka.common.serialization.StringDeserializer", serviceConfig.gameEventConsumer.consumerConfig()["key.deserializer"])
        assertEquals("org.apache.kafka.common.serialization.StringDeserializer", serviceConfig.gameEventConsumer.consumerConfig()["value.deserializer"])
        assertEquals("dealer", serviceConfig.gameEventConsumer.consumerConfig()["group.id"])
        assertEquals("earliest", serviceConfig.gameEventConsumer.consumerConfig()["auto.offset.reset"])
        assertEquals("true", serviceConfig.gameEventConsumer.consumerConfig()["enable.auto.commit"])

        assertEquals("dealer_stream", serviceConfig.eventStoreDBRepository.streamName)
        assertEquals(100, serviceConfig.eventStoreDBRepository.maxReadPageSize)
        assertEquals(200, serviceConfig.eventStoreDBRepository.maxWritePageSize)
        //assertDoesNotThrow { serviceConfig.eventStoreDBRepository.eventStoreDBClientSettings() }

        assertEquals("dealer_event", serviceConfig.dealerEventPublisher.topic())
        assertEquals("localhost:9093", serviceConfig.dealerEventPublisher.producerConfig()["bootstrap.servers"])
        assertEquals("org.apache.kafka.common.serialization.StringSerializer", serviceConfig.dealerEventPublisher.producerConfig()["key.serializer"])
        assertEquals("org.apache.kafka.common.serialization.StringSerializer", serviceConfig.dealerEventPublisher.producerConfig()["value.serializer"])
        assertEquals("1", serviceConfig.dealerEventPublisher.producerConfig()["acks"])

    }
}