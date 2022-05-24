package com.abaddon83.burraco.dealer.commands

import com.abaddon83.burraco.dealer.ports.EventsPort
import com.abaddon83.burraco.dealer.ports.EventsPortResult
import io.github.abaddon.kcqrs.core.IAggregate
import io.github.abaddon.kcqrs.core.domain.IAggregateCommandHandler
import io.github.abaddon.kcqrs.core.domain.Result
import io.github.abaddon.kcqrs.core.domain.messages.commands.ICommand
import io.github.abaddon.kcqrs.core.persistence.IAggregateRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class CommandHandler<TAggregate : IAggregate>(
    override val repository: IAggregateRepository<TAggregate>,
    private val eventsPort: EventsPort
) : IAggregateCommandHandler<TAggregate> {
    private val log: Logger = LoggerFactory.getLogger(this::class.simpleName)
    override suspend fun handle(
        command: ICommand<TAggregate>,
        updateHeaders: () -> Map<String, String>
    ): Result<Exception, TAggregate> =
        when (val actualAggregateResult = repository.getById(command.aggregateID)) {
            is Result.Valid -> {
                log.debug("Aggregate ${actualAggregateResult.value.id.valueAsString()} loaded from the repository")
                val newAggregate = command.execute(actualAggregateResult.value)
                when(val saveResult=repository.save(newAggregate, UUID.randomUUID(), updateHeaders)){
                    is Result.Valid -> {
                        log.debug("Aggregate ${newAggregate.id.valueAsString()} persisted")
                        val eventsPortResults=eventsPort.publish(newAggregate.uncommittedEvents())
                        val errors = eventsPortResults.filterIsInstance<EventsPortResult.Invalid<Exception>>()
                        when(errors.isEmpty()){
                            true -> {
                                log.debug("Events ${newAggregate.uncommittedEvents().joinToString(separator = ", "){ it.messageId.toString()}} published")
                            }
                            false -> {
                                log.error("Events ${newAggregate.uncommittedEvents().joinToString(separator = ", "){ it.messageId.toString()}} couldn't published",errors)
                            }
                        }
                        saveResult
                    }
                    is Result.Invalid -> {
                        log.error("Aggregate ${newAggregate.id.valueAsString()} didn't persist",saveResult.err)
                        saveResult
                    }
                }
            }
            is Result.Invalid -> {
                log.error("Aggregate ${command.aggregateID} didn't load",actualAggregateResult.err)
                actualAggregateResult
            }
        }

    override suspend fun handle(command: ICommand<TAggregate>): Result<Exception, TAggregate> =
        handle(command) { mapOf<String, String>() }
}