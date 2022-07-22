package com.abaddon83.burraco.dealer

import com.abaddon83.burraco.common.adapter.kafka.consumer.KafkaConsumerConfig
import com.abaddon83.burraco.common.adapter.kafka.producer.KafkaProducerConfig
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addFileSource
import io.github.abaddon.kcqrs.eventstoredb.eventstore.EventStoreDBRepositoryConfig
import java.io.File


data class ServiceConfig(
    val gameEventConsumer: KafkaConsumerConfig,
    val eventStoreDBRepository: EventStoreDBRepositoryConfig,
    val dealerEventPublisher: KafkaProducerConfig,

    ) {

    companion object {
        fun load(configPath: String): ServiceConfig =
            ConfigLoaderBuilder.default()
                //.addResourceSource(configPath)
                .addFileSource(File(configPath))
                .build()
                .loadConfigOrThrow<ServiceConfig>()
    }
}





