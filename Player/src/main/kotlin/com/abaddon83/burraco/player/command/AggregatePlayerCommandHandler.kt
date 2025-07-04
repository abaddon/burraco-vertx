package com.abaddon83.burraco.player.command

import com.abaddon83.burraco.player.event.PlayerEvent
import com.abaddon83.burraco.player.model.player.Player
import com.abaddon83.burraco.player.port.ExternalEventPublisherPort
import io.github.abaddon.kcqrs.core.domain.AggregateCommandHandler
import io.github.abaddon.kcqrs.core.persistence.IAggregateRepository

class AggregatePlayerCommandHandler(
    repository: IAggregateRepository<Player>,
    private val externalEventPublisherPort: ExternalEventPublisherPort,
) : AggregateCommandHandler<Player>(repository) {

    override suspend fun onSuccess(updatedAggregate: Player): Result<Player> = runCatching {
        updatedAggregate
            .uncommittedEvents()
            .map {
                when (it) {
                    is PlayerEvent -> externalEventPublisherPort.publish(updatedAggregate, it)
                    else -> null
                }
            }
        updatedAggregate
    }
}