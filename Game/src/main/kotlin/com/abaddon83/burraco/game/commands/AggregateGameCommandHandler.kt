package com.abaddon83.burraco.game.commands

import com.abaddon83.burraco.game.events.game.GameEvent
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.ports.ExternalEventPublisherPort
import io.github.abaddon.kcqrs.core.domain.AggregateCommandHandler
import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log
import io.github.abaddon.kcqrs.core.persistence.IAggregateRepository
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class AggregateGameCommandHandler(
    repository: IAggregateRepository<Game>,
    private val externalEventPublisherPort: ExternalEventPublisherPort,
    coroutineContext: CoroutineContext
) : AggregateCommandHandler<Game>(repository, coroutineContext) {

    override suspend fun onSuccess(updatedAggregate: Game): Result<Game> = withContext(coroutineContext) {
        runCatching {
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
}
