package com.abaddon83.burraco.game

import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
internal class ServiceConfigTest {

    @Test
    fun `test`() {
        val serviceConfig =
            ServiceConfig.load(Vertx.vertx(), { serviceConfig ->
                assertNotNull(serviceConfig)
                assertEquals("game-service", serviceConfig.restHttpService.serviceName)
                assertEquals(8080, serviceConfig.restHttpService.http.port)
                assertEquals("0.0.0.0", serviceConfig.restHttpService.http.address)
                assertEquals("/", serviceConfig.restHttpService.http.root)

                assertEquals("game-event", serviceConfig.kafkaGameProducer.topic())
                assertEquals("localhost:9092", serviceConfig.kafkaGameProducer.producerConfig()["bootstrap.servers"])
                assertEquals(
                    "org.apache.kafka.common.serialization.StringSerializer",
                    serviceConfig.kafkaGameProducer.producerConfig()["key.serializer"]
                )
                assertEquals(
                    "org.apache.kafka.common.serialization.StringSerializer",
                    serviceConfig.kafkaGameProducer.producerConfig()["value.serializer"]
                )
                assertEquals("1", serviceConfig.kafkaGameProducer.producerConfig()["acks"])

                assertEquals("stream_name", serviceConfig.eventStore.streamName)
                assertEquals(100, serviceConfig.eventStore.maxReadPageSize)
                assertEquals(200, serviceConfig.eventStore.maxWritePageSize)
            })


        //assertDoesNotThrow { serviceConfig.eventStoreDBRepository.eventStoreDBClientSettings() }
    }
}