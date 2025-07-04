package com.abaddon83.burraco.game.adapters.commandController.kafka

import com.abaddon83.burraco.common.adapter.kafka.consumer.KafkaConsumerConfig
import com.abaddon83.burraco.game.ServiceConfig
import com.abaddon83.burraco.game.adapters.commandController.CommandControllerAdapter
import com.abaddon83.burraco.game.adapters.commandController.rest.config.RestHttpServiceConfig
import com.abaddon83.burraco.game.adapters.eventStore.config.EventStoreConfig
import com.abaddon83.burraco.game.commands.AggregateGameCommandHandler
import com.abaddon83.burraco.game.helpers.DummyExternalEventPublisherAdapter
import com.abaddon83.burraco.game.models.game.GameDraft
import com.abaddon83.burraco.common.adapter.kafka.producer.KafkaProducerConfig
import io.github.abaddon.kcqrs.core.persistence.InMemoryEventStoreRepository
import io.github.abaddon.kcqrs.eventstoredb.eventstore.config.EventStoreDBRepositoryConfig
import io.github.abaddon.kcqrs.eventstoredb.eventstore.config.EventStoreDBSettings
import io.vertx.junit5.VertxExtension
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
internal class KafkaPlayerConsumerVerticleTest {

    private fun createTestServiceConfig(): ServiceConfig {
        val restHttpServiceConfig = RestHttpServiceConfig(
            serviceName = "test-service",
            openApiPath = "test.yaml",
            http = RestHttpServiceConfig.HttpConfig(
                port = 8080,
                address = "localhost",
                root = "/"
            )
        )
        
        val kafkaConsumerConfig = KafkaConsumerConfig(
            topic = "test-topic",
            properties = mapOf(
                "bootstrap.servers" to "localhost:9092",
                "group.id" to "test-group",
                "auto.offset.reset" to "earliest",
                "enable.auto.commit" to "false",
                "key.deserializer" to "org.apache.kafka.common.serialization.StringDeserializer",
                "value.deserializer" to "org.apache.kafka.common.serialization.StringDeserializer"
            )
        )
        
        val kafkaProducerConfig = KafkaProducerConfig(
            topic = "test-topic",
            properties = mapOf(
                "bootstrap.servers" to "localhost:9092",
                "key.serializer" to "org.apache.kafka.common.serialization.StringSerializer",
                "value.serializer" to "org.apache.kafka.common.serialization.StringSerializer"
            )
        )
        
        val eventStoreConfig = EventStoreConfig(
            streamName = "test-stream",
            maxReadPageSize = 100,
            maxWritePageSize = 200,
            eventStoreDB = EventStoreDBSettings(connectionString = "test://localhost:2113")
        )
        
        return ServiceConfig(
            restHttpService = restHttpServiceConfig,
            kafkaDealerConsumer = kafkaConsumerConfig,
            kafkaPlayerConsumer = kafkaConsumerConfig,
            kafkaGameProducer = kafkaProducerConfig,
            eventStore = eventStoreConfig
        )
    }

    @Test
    fun `Given a KafkaPlayerConsumerVerticle, when loadHandlers is called, then handlers are correctly configured`() {
        val serviceConfig = createTestServiceConfig()
        
        val inMemoryEventStoreRepository = InMemoryEventStoreRepository(
            "stream",
            { GameDraft.empty() }
        )
        val commandHandler = AggregateGameCommandHandler(
            inMemoryEventStoreRepository,
            DummyExternalEventPublisherAdapter()
        )
        val commandControllerAdapter = CommandControllerAdapter(commandHandler)
        
        val kafkaPlayerConsumerVerticle = KafkaPlayerConsumerVerticle(
            serviceConfig,
            commandControllerAdapter
        )
        
        val handlers = kafkaPlayerConsumerVerticle.loadHandlers()
        
        assertNotNull(handlers)
    }
}