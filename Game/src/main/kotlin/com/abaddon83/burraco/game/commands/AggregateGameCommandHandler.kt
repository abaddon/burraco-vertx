package com.abaddon83.burraco.game.commands

import com.abaddon83.burraco.game.events.game.GameEvent
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.ports.GameEventPublisherPort
import io.github.abaddon.kcqrs.core.domain.IAggregateCommandHandler
import io.github.abaddon.kcqrs.core.domain.Result
import io.github.abaddon.kcqrs.core.domain.messages.commands.ICommand
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.github.abaddon.kcqrs.core.persistence.IAggregateRepository
import java.util.*

class AggregateGameCommandHandler(
    override val repository: IAggregateRepository<Game>,
    private val gameEventPublisherPort: GameEventPublisherPort
) : IAggregateCommandHandler<Game> {
    override suspend fun handle(
        command: ICommand<Game>,
        updateHeaders: () -> Map<String, String>
    ): Result<Exception, Game> =
        when (val actualAggregateResult = repository.getById(command.aggregateID)) {
            is Result.Valid -> {
                try {
                    val newAggregate = command.execute(actualAggregateResult.value)
                    check(newAggregate.version > actualAggregateResult.value.version) { "Aggregate version is wrong" }
                    val saveResult = repository.save(newAggregate, UUID.randomUUID(), updateHeaders)
                    when (saveResult) {
                        is Result.Valid -> publishExternalEvent(saveResult.value)
                        else -> null
                    }
                    saveResult
                } catch (ex: IllegalArgumentException) {
                    Result.Invalid(ex)
                } catch (ex: UnsupportedOperationException){
                    Result.Invalid(ex)
                }
            }
            is Result.Invalid -> actualAggregateResult
        }

    override suspend fun handle(command: ICommand<Game>): Result<Exception, Game> =
        handle(command) { mapOf<String, String>() }

    private suspend fun publishExternalEvent(aggregate: Game) {
        aggregate
            .uncommittedEvents()
            .forEach {
                when (it) {
                    is GameEvent -> gameEventPublisherPort.publish(aggregate, it)
                }
            }
    }
}