package com.abaddon83.burraco.game

import com.abaddon83.burraco.common.adapter.kafka.consumer.KafkaConsumerConfig
import com.abaddon83.burraco.common.adapter.kafka.producer.KafkaProducerConfig
import com.abaddon83.burraco.game.adapters.eventStore.config.EventStoreConfig
import com.abaddon83.burraco.game.adapters.commandController.rest.config.RestHttpServiceConfig
import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log
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
    val kafkaDealerConsumer: KafkaConsumerConfig,
    val kafkaGameProducer: KafkaProducerConfig,
    val eventStore: EventStoreConfig
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