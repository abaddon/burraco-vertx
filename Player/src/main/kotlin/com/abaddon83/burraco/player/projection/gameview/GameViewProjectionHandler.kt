package com.abaddon83.burraco.player.projection.gameview

import com.abaddon83.burraco.common.adapter.kafka.projection.KafkaStoreProjectionHandler
import com.abaddon83.burraco.common.models.event.game.GameCreated
import com.abaddon83.burraco.common.models.event.game.PlayerAdded
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.github.abaddon.kcqrs.core.helpers.KcqrsLoggerFactory.log
import io.github.abaddon.kcqrs.core.persistence.IProjectionRepository
import io.github.abaddon.kcqrs.core.projections.IProjectionKey

class GameViewProjectionHandler(
    repository: IProjectionRepository<GameView>
) : KafkaStoreProjectionHandler<GameView>(
    repository
) {
    override fun getProjectionKey(event: IDomainEvent): IProjectionKey {
        return when (event) {
            is GameCreated -> GameViewKey(event.aggregateId)
            is PlayerAdded -> GameViewKey(event.aggregateId)
            else -> GameViewKey.empty()
        }
    }

    override fun filterProcessedEvent(currentProjection: GameView, event: IDomainEvent): IDomainEvent? {
        log.info("Filtering event ${event::class.simpleName} with version ${event.version} for projection with last processed event version ${currentProjection.lastProcessedEvent[event.aggregateType] ?: 0}")
        return event
    }
}
