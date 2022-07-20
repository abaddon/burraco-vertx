package com.abaddon83.burraco.game

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ServiceConfigTest {

    @Test
    fun `test`() {
        val serviceConfig =
            ServiceConfig.load("/Users/stefanolonghi/Documents/src/burraco-vertx/Game/src/test/resources/dev.yml")

        assertEquals("game-service", serviceConfig.restHttpService.serviceName)
        assertEquals(8080, serviceConfig.restHttpService.http.port)
        assertEquals("0.0.0.0", serviceConfig.restHttpService.http.address)
        assertEquals("/", serviceConfig.restHttpService.http.root)

        assertEquals("game-event", serviceConfig.gameEventPublisher.topic())
        assertEquals("localhost:9092", serviceConfig.gameEventPublisher.producerConfig()["bootstrap.servers"])
        assertEquals(
            "org.apache.kafka.common.serialization.StringSerializer",
            serviceConfig.gameEventPublisher.producerConfig()["key.serializer"]
        )
        assertEquals(
            "org.apache.kafka.common.serialization.StringSerializer",
            serviceConfig.gameEventPublisher.producerConfig()["value.serializer"]
        )
        assertEquals("1", serviceConfig.gameEventPublisher.producerConfig()["acks"])

        assertEquals("stream_name", serviceConfig.eventStoreDBRepository.streamName)
        assertEquals(100, serviceConfig.eventStoreDBRepository.maxReadPageSize)
        assertEquals(200, serviceConfig.eventStoreDBRepository.maxWritePageSize)

        //assertDoesNotThrow { serviceConfig.eventStoreDBRepository.eventStoreDBClientSettings() }
    }
}