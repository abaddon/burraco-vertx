package com.abaddon83.burraco.dealer

import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
internal class ServiceConfigTest {

    @Test
    fun test(vertx: Vertx, testContext: io.vertx.junit5.VertxTestContext) {
        ServiceConfig.load(vertx, { serviceConfig ->
            testContext.verify {
                assertEquals("game", serviceConfig.gameEventConsumer.topic())
                assertEquals("localhost:19092", serviceConfig.gameEventConsumer.consumerConfig()["bootstrap.servers"])
                assertEquals(
                    "org.apache.kafka.common.serialization.StringDeserializer",
                    serviceConfig.gameEventConsumer.consumerConfig()["key.deserializer"]
                )
                assertEquals(
                    "org.apache.kafka.common.serialization.StringDeserializer",
                    serviceConfig.gameEventConsumer.consumerConfig()["value.deserializer"]
                )
                assertEquals("dealer", serviceConfig.gameEventConsumer.consumerConfig()["group.id"])
                assertEquals("earliest", serviceConfig.gameEventConsumer.consumerConfig()["auto.offset.reset"])
                assertEquals("true", serviceConfig.gameEventConsumer.consumerConfig()["enable.auto.commit"])

                assertEquals("dealer_stream", serviceConfig.eventStore.streamName)
                assertEquals(100, serviceConfig.eventStore.maxReadPageSize)
                assertEquals(200, serviceConfig.eventStore.maxWritePageSize)
                //assertDoesNotThrow { serviceConfig.eventStoreDBRepository.eventStoreDBClientSettings() }

                assertEquals("dealer", serviceConfig.dealerEventPublisher.topic())
                assertEquals("localhost:19092", serviceConfig.dealerEventPublisher.producerConfig()["bootstrap.servers"])
                assertEquals(
                    "org.apache.kafka.common.serialization.StringSerializer",
                    serviceConfig.dealerEventPublisher.producerConfig()["key.serializer"]
                )
                assertEquals(
                    "org.apache.kafka.common.serialization.StringSerializer",
                    serviceConfig.dealerEventPublisher.producerConfig()["value.serializer"]
                )
                assertEquals("-1", serviceConfig.dealerEventPublisher.producerConfig()["acks"])
            }
            testContext.completeNow()
        })
    }
}