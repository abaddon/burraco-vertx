package com.abaddon83.burraco.game

import com.abaddon83.burraco.game.adapters.commandController.config.RestApiConfig
import com.abaddon83.burraco.game.adapters.dealerAdapter.config.KafkaDealerConsumerConfig
import com.abaddon83.burraco.game.adapters.eventBrokerProducerAdapter.config.KafkaGameProducerConfig
import com.abaddon83.burraco.game.adapters.eventStoreAdapter.tcp.config.EventStoreTcpBusConfig
import com.typesafe.config.ConfigFactory
import io.github.config4k.extract
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject


data class ServiceConfig(
    val restApi: RestApiConfig,
    val kafkaDealerConsumer: KafkaDealerConsumerConfig,
    val kafkaGameProducer: KafkaGameProducerConfig,
    val eventStoreTcpBus: EventStoreTcpBusConfig
    ) {

    fun toJson(): JsonObject =
        buildJsonObject {
            put("restApi",restApi.toJson())
            put("eventStoreTcpBus",eventStoreTcpBus.toJson())
        }

    companion object {
        fun load(): ServiceConfig {
            val config = ConfigFactory.load()
            return config.extract<ServiceConfig>()
        }
    }
}




