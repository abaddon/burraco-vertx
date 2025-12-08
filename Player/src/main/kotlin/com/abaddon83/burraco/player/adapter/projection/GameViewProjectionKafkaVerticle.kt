package com.abaddon83.burraco.player.adapter.projection

import com.abaddon83.burraco.common.adapter.kafka.consumer.EventRouterHandler
import com.abaddon83.burraco.common.adapter.kafka.consumer.KafkaConsumerVerticle
import com.abaddon83.burraco.player.ServiceConfig
import com.abaddon83.burraco.player.adapter.projection.handler.GameCreatedProjectionKafkaEventHandler
import com.abaddon83.burraco.player.adapter.projection.handler.GameStartedProjectionKafkaEventHandler
import com.abaddon83.burraco.player.adapter.projection.handler.PlayerAddedProjectionKafkaEventHandler
import com.abaddon83.burraco.player.projection.gameview.GameView
import com.abaddon83.burraco.player.projection.gameview.GameViewProjectionHandler
import io.github.abaddon.kcqrs.core.persistence.IProjectionRepository


class GameViewProjectionKafkaVerticle(
    serviceConfig: ServiceConfig,
    private val repository: IProjectionRepository<GameView>
) : KafkaConsumerVerticle(serviceConfig.kafkaGameProjectionConsumer) {

    override fun loadHandlers(): EventRouterHandler {
        // Create single projection handler instance
        val projectionHandler = GameViewProjectionHandler(repository)

        // Register handlers with router
        return EventRouterHandler()
            .addHandler(GameCreatedProjectionKafkaEventHandler(projectionHandler))
            .addHandler(PlayerAddedProjectionKafkaEventHandler(projectionHandler))
            .addHandler(GameStartedProjectionKafkaEventHandler(projectionHandler))
    }
}