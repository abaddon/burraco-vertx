package com.abaddon83.burraco.player.adapter.projection

import com.abaddon83.burraco.common.adapter.kafka.consumer.EventRouterHandler
import com.abaddon83.burraco.common.adapter.kafka.consumer.KafkaConsumerVerticle
import com.abaddon83.burraco.player.ServiceConfig
import com.abaddon83.burraco.player.adapter.projection.handler.GameCreatedProjectionKafkaEventHandler
import com.abaddon83.burraco.player.adapter.projection.handler.PlayerAddedProjectionKafkaEventHandler
import com.abaddon83.burraco.player.projection.gameview.GameView
import com.abaddon83.burraco.player.projection.gameview.GameViewProjectionHandler
import io.github.abaddon.kcqrs.core.persistence.IProjectionRepository

/**
 * Vert.x verticle that integrates GameViewProjectionHandler with Kafka consumer infrastructure.
 *
 * @param serviceConfig Application configuration containing Kafka consumer settings
 * @param repository Repository for persisting GameView projections
 */
class GameViewProjectionKafkaVerticle(
    serviceConfig: ServiceConfig,
    private val repository: IProjectionRepository<GameView>
) : KafkaConsumerVerticle(serviceConfig.kafkaGameConsumer) {

    /**
     * Loads and configures event handlers for this Kafka consumer.
     *
     * Creates a single GameViewProjectionHandler instance and wraps it in
     * ProjectionKafkaEventHandler adapters for each event type the projection handles.
     *
     * Event types handled:
     * - GameCreated: Creates new GameView projection
     * - PlayerAdded: Updates existing GameView with player information
     *
     * @return EventRouterHandler configured with projection event handlers
     */
    override fun loadHandlers(): EventRouterHandler {
        // Create single projection handler instance
        val projectionHandler = GameViewProjectionHandler(repository)

        // Register handlers with router
        return EventRouterHandler()
            .addHandler(GameCreatedProjectionKafkaEventHandler(projectionHandler))
            .addHandler(PlayerAddedProjectionKafkaEventHandler(projectionHandler))
    }
}