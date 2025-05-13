package com.abaddon83.burraco.common.commandController

import com.abaddon83.burraco.common.helpers.log
import io.github.abaddon.kcqrs.core.domain.AggregateRoot
import io.github.abaddon.kcqrs.core.domain.IAggregateCommandHandler
import io.github.abaddon.kcqrs.core.domain.Result
import io.github.abaddon.kcqrs.core.domain.messages.commands.ICommand
import java.util.*

abstract class AggregateCommandHandler<A: AggregateRoot>: IAggregateCommandHandler<A> {
    override suspend fun handle(
        command: ICommand<A>,
        updateHeaders: () -> Map<String, String>
    ): Result<Exception, A> =
        when (val actualAggregateResult = repository.getById(command.aggregateID)) {
            is Result.Valid -> {
                log.debug("Aggregate ${actualAggregateResult.value.id.valueAsString()} loaded from the repository")
                try {
                    val newAggregate = command.execute(actualAggregateResult.value)
                    check(newAggregate.version > actualAggregateResult.value.version) { "Aggregate version is wrong" }

                    when (val saveResult = repository.save(newAggregate, UUID.randomUUID(), updateHeaders)) {
                        is Result.Valid -> {
                            log.debug("Aggregate ${saveResult.value.id.valueAsString()} persisted")
                            onSuccess(saveResult.value)
                            saveResult
                        }
                        is Result.Invalid ->{
                            log.error("Aggregate ${newAggregate.id.valueAsString()} not persisted",saveResult.err)
                            saveResult
                        }
                    }
                } catch (ex: IllegalArgumentException) {
                    log.error("Command ${command::class.java.simpleName} execution failed",ex)
                    Result.Invalid(ex)
                } catch (ex: UnsupportedOperationException){
                    log.error("Command ${command::class.java.simpleName} execution failed",ex)
                    Result.Invalid(ex)
                } catch (ex: Exception){
                    log.error("Generic exception raised ${command::class.java.simpleName} execution failed",ex)
                    Result.Invalid(ex)
                }
            }
            is Result.Invalid ->{
                log.error("Loading Aggregate ${command.aggregateID} failed", actualAggregateResult.err)
                actualAggregateResult
            }


        }

    override suspend fun handle(command: ICommand<A>): Result<Exception, A> =
        handle(command) { mapOf<String, String>() }

    protected abstract suspend fun onSuccess(aggregate: A)
}