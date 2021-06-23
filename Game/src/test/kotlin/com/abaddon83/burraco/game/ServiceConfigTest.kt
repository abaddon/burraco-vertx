package com.abaddon83.burraco.game

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ServiceConfigTest{

    @Test
    fun `load config`(){
        val serviceConfig = ServiceConfig.load()

        //read RestApi Config
        assertEquals(8080,serviceConfig.restApi.http.port)
        assertEquals("localhost",serviceConfig.restApi.http.address)
        assertEquals("/",serviceConfig.restApi.http.root)

        //read KafkaDealerConsumer Config
        assertNotNull(serviceConfig.kafkaDealerConsumer.properties.get("bootstrap.servers"))
        assertNotNull(serviceConfig.kafkaDealerConsumer.properties.get("group.id"))
        assertNotNull(serviceConfig.kafkaDealerConsumer.properties.get("auto.offset.reset"))
        assertNotNull(serviceConfig.kafkaDealerConsumer.properties.get("enable.auto.commit"))

        //read KafkaGameProducerConfig Config
        assertNotNull(serviceConfig.kafkaGameProducer.properties.get("bootstrap.servers"))
        assertNotNull(serviceConfig.kafkaGameProducer.properties.get("key.serializer"))
        assertNotNull(serviceConfig.kafkaGameProducer.properties.get("value.serializer"))
        assertNotNull(serviceConfig.kafkaGameProducer.properties.get("acks"))

        //read eventStoreTcpBus Config
        assertEquals("localhost", serviceConfig.eventStoreTcpBus.service.address)
        assertEquals(8080, serviceConfig.eventStoreTcpBus.service.port)
        assertEquals("eventstore-bus-publish", serviceConfig.eventStoreTcpBus.channels.publish)
        assertEquals("eventstore-bus-query", serviceConfig.eventStoreTcpBus.channels.query)
    }
}