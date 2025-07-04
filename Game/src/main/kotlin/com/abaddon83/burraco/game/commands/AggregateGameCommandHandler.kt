package com.abaddon83.burraco.game.commands

import com.abaddon83.burraco.common.models.event.game.GameEvent
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.ports.ExternalEventPublisherPort
import io.github.abaddon.kcqrs.core.domain.AggregateCommandHandler
import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log
import io.github.abaddon.kcqrs.core.persistence.IAggregateRepository

class AggregateGameCommandHandler(
    repository: IAggregateRepository<Game>,
    private val externalEventPublisherPort: ExternalEventPublisherPort,
) : AggregateCommandHandler<Game>(repository) {

    override suspend fun onSuccess(updatedAggregate: Game): Result<Game> = runCatching {
        updatedAggregate
            .uncommittedEvents()
            .map {
                when (it) {
                    is GameEvent -> externalEventPublisherPort.publish(updatedAggregate, it)
                    else -> null
                }
            }
        updatedAggregate
    }.onFailure { ex ->
        log.error("Error while publishing events", ex)
    }
}

