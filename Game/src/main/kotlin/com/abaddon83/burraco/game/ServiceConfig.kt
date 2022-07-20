package com.abaddon83.burraco.game

import com.abaddon83.burraco.game.adapters.commandController.rest.config.RestHttpServiceConfig
import com.abaddon83.burraco.game.adapters.gameEventPublisher.kafka.KafkaProducerConfig
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addFileSource
import com.sksamuel.hoplite.addResourceSource
import io.github.abaddon.kcqrs.eventstoredb.eventstore.EventStoreDBRepositoryConfig
import java.io.File


data class ServiceConfig(
    val restHttpService: RestHttpServiceConfig,
    val gameEventPublisher: KafkaProducerConfig,
    val eventStoreDBRepository: EventStoreDBRepositoryConfig
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





