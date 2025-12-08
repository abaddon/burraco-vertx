package com.abaddon83.burraco.player.adapter.projection.handler

import com.abaddon83.burraco.common.adapter.kafka.projection.KafkaStoreProjectionHandler
import com.abaddon83.burraco.common.adapter.kafka.projection.ProjectionKafkaEventHandler
import com.abaddon83.burraco.common.externalEvents.KafkaEvent
import com.abaddon83.burraco.common.externalEvents.game.GameStartedExternalEvent
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.github.abaddon.kcqrs.core.projections.IProjection
import io.vertx.core.json.Json

/**
 * Projection handler for GameStarted events.
 *
 * Updates GameView projection to reflect that the game has started
 * and transitioned to PLAYING state.
 */
class GameStartedProjectionKafkaEventHandler<TProjection : IProjection>(
    projectionHandler: KafkaStoreProjectionHandler<TProjection>
) :
    ProjectionKafkaEventHandler<TProjection>(projectionHandler, "GameStarted") {

    override fun toDomainEvent(kafkaEvent: KafkaEvent): IDomainEvent {
        return Json.decodeValue(kafkaEvent.eventPayload, GameStartedExternalEvent::class.java).toDomain()
    }
}
