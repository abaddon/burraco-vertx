package com.abaddon83.burraco.player.projection.gameview

import com.abaddon83.burraco.common.models.event.game.GameCreated
import com.abaddon83.burraco.common.models.event.game.PlayerAdded
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.github.abaddon.kcqrs.core.persistence.IProjectionRepository
import io.github.abaddon.kcqrs.core.projections.IProjectionKey
import io.github.abaddon.kcqrs.eventstoredb.projection.EventStoreProjectionHandler

class GameViewProjectionHandler(
    repository: IProjectionRepository<GameView>
) : EventStoreProjectionHandler<GameView>(
    repository
) {
    override fun getProjectionKey(event: IDomainEvent): IProjectionKey {
        return when (event) {
            is GameCreated -> GameViewKey(event.aggregateId)
            is PlayerAdded -> GameViewKey(event.aggregateId)
            else -> GameViewKey.empty()
        }
    }

}
