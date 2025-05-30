package com.abaddon83.burraco.game

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
                assertEquals("game-service", serviceConfig.restHttpService.serviceName)
                assertEquals(8081, serviceConfig.restHttpService.http.port)
                assertEquals("0.0.0.0", serviceConfig.restHttpService.http.address)
                assertEquals("/", serviceConfig.restHttpService.http.root)

                assertEquals("game", serviceConfig.kafkaGameProducer.topic())
                assertEquals("localhost:19092", serviceConfig.kafkaGameProducer.producerConfig()["bootstrap.servers"])
                assertEquals(
                    "org.apache.kafka.common.serialization.StringSerializer",
                    serviceConfig.kafkaGameProducer.producerConfig()["key.serializer"]
                )
                assertEquals(
                    "org.apache.kafka.common.serialization.StringSerializer",
                    serviceConfig.kafkaGameProducer.producerConfig()["value.serializer"]
                )
                assertEquals("-1", serviceConfig.kafkaGameProducer.producerConfig()["acks"])

                assertEquals("game_stream", serviceConfig.eventStore.streamName)
                assertEquals(100, serviceConfig.eventStore.maxReadPageSize)
                assertEquals(200, serviceConfig.eventStore.maxWritePageSize)
            }
            testContext.completeNow()
        })
    }
}