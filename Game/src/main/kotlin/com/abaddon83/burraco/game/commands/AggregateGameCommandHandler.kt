package com.abaddon83.burraco.game.commands

import com.abaddon83.burraco.common.commandController.AggregateCommandHandler
import com.abaddon83.burraco.game.events.game.GameEvent
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.ports.ExternalEventPublisherPort
import io.github.abaddon.kcqrs.core.persistence.IAggregateRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AggregateGameCommandHandler(
    override val repository: IAggregateRepository<Game>,
    private val externalEventPublisherPort: ExternalEventPublisherPort
) : AggregateCommandHandler<Game>() {
    private val log: Logger = LoggerFactory.getLogger(this::class.simpleName)


    protected override suspend fun onSuccess(aggregate: Game) {
        aggregate
            .uncommittedEvents()
            .forEach {
                when (it) {
                    is GameEvent -> externalEventPublisherPort.publish(aggregate, it)
                }
            }
    }
}