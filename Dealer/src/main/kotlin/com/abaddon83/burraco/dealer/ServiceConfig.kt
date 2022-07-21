package com.abaddon83.burraco.dealer

import com.abaddon83.burraco.common.adapter.kafka.consumer.KafkaConsumerConfig
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addFileSource
import io.github.abaddon.kcqrs.eventstoredb.eventstore.EventStoreDBRepositoryConfig
import java.io.File


data class ServiceConfig(
    val gameEventConsumer: KafkaConsumerConfig,
) {

    companion object {
        fun load(configPath: String = "dev.yml"): ServiceConfig =
            ConfigLoaderBuilder.default()
                //.addResourceSource(configPath)
                .addFileSource(File(configPath))
                .build()
                .loadConfigOrThrow<ServiceConfig>()
    }
}





