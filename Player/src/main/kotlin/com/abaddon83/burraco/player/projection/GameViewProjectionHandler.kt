package com.abaddon83.burraco.player.projection

import com.abaddon83.burraco.common.models.event.game.GameCreated
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
            is GameCreated -> return GameViewKey(event.aggregateId)
            else -> GameViewKey.empty()
        }
    }

}
