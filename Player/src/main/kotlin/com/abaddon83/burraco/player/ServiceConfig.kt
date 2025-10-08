package com.abaddon83.burraco.player

import com.abaddon83.burraco.common.adapter.kafka.consumer.KafkaConsumerConfig
import com.abaddon83.burraco.common.adapter.kafka.producer.KafkaProducerConfig
import com.abaddon83.burraco.player.adapter.commandController.rest.RestHttpServiceConfig
import com.abaddon83.burraco.player.adapter.eventstore.EventStoreConfig
import com.abaddon83.burraco.player.adapter.projection.GameViewProjectionConfig
import com.abaddon83.burraco.player.adapter.projection.PlayerViewProjectionConfig
import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log
import io.github.abaddon.kcqrs.eventstoredb.config.EventStoreDBConfig
import io.github.abaddon.kcqrs.eventstoredb.config.SubscriptionFilterConfig
import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.Vertx
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ServiceConfig(
    val restHttpService: RestHttpServiceConfig,
    val kafkaGameConsumer: KafkaConsumerConfig,
    val kafkaDealerConsumer: KafkaConsumerConfig,
    val kafkaPlayerProducer: KafkaProducerConfig,
    val eventStore: EventStoreConfig,
    val gameProjection: GameViewProjectionConfig,
    val playerViewProjection: PlayerViewProjectionConfig
) {
    companion object {
        private val json = Json { ignoreUnknownKeys = true }

        fun load(vertx: Vertx, callback: (ServiceConfig) -> Unit) {
            val hoconStore = ConfigStoreOptions()
                .setType("file")
                .setFormat("hocon")
                .setConfig(json {
                    obj("path" to "application.conf")
                })
            val options = ConfigRetrieverOptions()
                .addStore(hoconStore)

            val retriever = ConfigRetriever.create(vertx, options)

            retriever.config.map { config ->
                val serviceConfig: ServiceConfig = json.decodeFromString(config.encode())
                log.info("serviceConfig: $serviceConfig")
                callback(serviceConfig)
            }
                .onFailure { error ->
                    log.error("Failed to load configuration: ${error.message}")
                    throw RuntimeException("Failed to load configuration", error)
                }
        }
    }
}

@Serializable
data class RestApiHttpConfig(
    val port: Int,
    val address: String,
    val root: String
)

@Serializable
data class GameProjectionConfig(
    val gameStreamName: String,
    val eventStoreConnectionString: String,
    val startFromBeginning: Boolean = true
) {
    fun getEventStoreDBConfig(): EventStoreDBConfig = EventStoreDBConfig(eventStoreConnectionString)
    
    
    fun getSubscriptionFilterConfig(): SubscriptionFilterConfig? = null // No filter for now
    
    companion object {
        fun empty(): GameProjectionConfig = GameProjectionConfig("", "", true)
    }
}