package com.abaddon83.burraco.player

import com.abaddon83.burraco.common.adapter.kafka.consumer.KafkaConsumerConfig
import com.abaddon83.burraco.common.adapter.kafka.producer.KafkaProducerConfig
import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log
import io.github.abaddon.kcqrs.eventstoredb.config.EventStoreDBConfig
import io.github.abaddon.kcqrs.eventstoredb.eventstore.EventStoreDBRepositoryConfig
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
    val kafkaPlayerProducer: KafkaProducerConfig,
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

@Serializable
data class RestHttpServiceConfig(
    val serviceName: String,
    val openApiPath: String,
    val http: RestApiHttpConfig
) {
    fun getHttpServerOptions(): io.vertx.core.http.HttpServerOptions {
        return io.vertx.core.http.HttpServerOptions()
            .setPort(http.port)
            .setHost(http.address)
    }
}

@Serializable
data class RestApiHttpConfig(
    val port: Int,
    val address: String,
    val root: String
)

@Serializable
data class EventStoreConfig(
    val streamName: String,
    val maxReadPageSize: Long,
    val maxWritePageSize: Int,
    val eventStoreDB: EventStoreDB
) {

    fun eventStoreDBRepositoryConfig(): EventStoreDBRepositoryConfig =
        EventStoreDBRepositoryConfig(
            EventStoreDBConfig(
                eventStoreDB.connectionString
            ),
            streamName,
            maxReadPageSize,
            maxWritePageSize
        )

    @Serializable
    data class EventStoreDB(
        val connectionString: String
    )

    companion object {
        fun empty(): EventStoreConfig = EventStoreConfig("", 1, 1, EventStoreDB(""))
    }
}