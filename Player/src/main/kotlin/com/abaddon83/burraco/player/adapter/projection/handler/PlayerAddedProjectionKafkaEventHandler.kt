package com.abaddon83.burraco.player.adapter.projection.handler

import com.abaddon83.burraco.common.adapter.kafka.projection.KafkaStoreProjectionHandler
import com.abaddon83.burraco.common.adapter.kafka.projection.ProjectionKafkaEventHandler
import com.abaddon83.burraco.common.externalEvents.KafkaEvent
import com.abaddon83.burraco.common.externalEvents.game.PlayerAddedExternalEvent
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.github.abaddon.kcqrs.core.projections.IProjection
import io.vertx.core.json.Json

class PlayerAddedProjectionKafkaEventHandler<TProjection : IProjection>(
    projectionHandler: KafkaStoreProjectionHandler<TProjection>
) :
    ProjectionKafkaEventHandler<TProjection>(projectionHandler, "PlayerAdded") {

    override fun toDomainEvent(kafkaEvent: KafkaEvent): IDomainEvent {
        return Json.decodeValue(kafkaEvent.eventPayload, PlayerAddedExternalEvent::class.java).toDomain()
    }

}