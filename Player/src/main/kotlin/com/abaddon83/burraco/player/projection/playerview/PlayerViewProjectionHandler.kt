package com.abaddon83.burraco.player.projection.playerview

import com.abaddon83.burraco.common.models.event.player.PlayerCollectedCard
import com.abaddon83.burraco.common.models.event.player.PlayerCreated
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.github.abaddon.kcqrs.core.helpers.KcqrsLoggerFactory.log
import io.github.abaddon.kcqrs.core.persistence.IProjectionRepository
import io.github.abaddon.kcqrs.core.projections.IProjectionKey
import io.github.abaddon.kcqrs.eventstoredb.projection.EventStoreProjectionHandler

class PlayerViewProjectionHandler(
    repository: IProjectionRepository<PlayerView>
) : EventStoreProjectionHandler<PlayerView>(
    repository
) {
    override fun getProjectionKey(event: IDomainEvent): IProjectionKey {
        return when (event) {
            is PlayerCreated -> PlayerViewKey(event.aggregateId, event.gameIdentity)
            is PlayerCollectedCard -> PlayerViewKey(event.aggregateId, event.gameId)
            else -> PlayerViewKey.empty()
        }
    }

    override fun filterProcessedEvent(currentProjection: PlayerView, event: IDomainEvent): IDomainEvent? {
        log.info("Filtering event ${event::class.simpleName} with version ${event.version} for projection with last processed event version ${currentProjection.lastProcessedEvent[event.aggregateType] ?: 0}")
        return event
    }
}
