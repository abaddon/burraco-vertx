//package com.abaddon83.burraco.common.commandController
//
//import com.abaddon83.burraco.common.helpers.flatMap
//import com.abaddon83.burraco.common.helpers.log
//import io.github.abaddon.kcqrs.core.domain.AggregateRoot
//import io.github.abaddon.kcqrs.core.domain.IAggregateCommandHandler
//import io.github.abaddon.kcqrs.core.domain.messages.commands.ICommand
//import kotlinx.coroutines.withContext
//import java.util.*
//import kotlin.coroutines.CoroutineContext
//
//abstract class AggregateCommandHandler<A : AggregateRoot>(
//    protected val coroutineContext: CoroutineContext
//) : AggregateCommandHandler<A> {
//
//    override suspend fun handle(
//        command: ICommand<A>,
//        updateHeaders: () -> Map<String, String>
//    ): Result<A> = withContext(coroutineContext) {
//        repository.getById(command.aggregateID)
//            .flatMap { actualAggregate ->
//                log.debug("Aggregate ${actualAggregate.id.valueAsString()} loaded from the repository")
//                val newAggregate = command.execute(actualAggregate)
//                check(newAggregate.version > actualAggregate.version) { "Aggregate version is wrong" }
//                repository.save(newAggregate, UUID.randomUUID(), updateHeaders)
//            }.flatMap { actualAggregate ->
//                log.debug("Aggregate ${actualAggregate.id.valueAsString()} saved to the repository")
//                val result = onSuccess(actualAggregate)
//                if (result.isFailure) {
//                    Result.failure(result.exceptionOrNull()!!)
//                } else {
//                    Result.success(actualAggregate)
//                }
//            }
//    }
//
//
//    //log.error("Aggregate ${newAggregate.id.valueAsString()} not persisted",saveResult.err)
////                    when (val saveResult = repository.save(newAggregate, UUID.randomUUID(), updateHeaders)) {
////                        is Result.Valid -> {
////
////                            saveResult
////                        }
////                        is Result.Invalid ->{
////                            log.error("Aggregate ${newAggregate.id.valueAsString()} not persisted",saveResult.err)
////                            saveResult
////                        }
////                    }
////                } catch (ex: IllegalArgumentException) {
////                    log.error("Command ${command::class.java.simpleName} execution failed",ex)
////                    Result.Invalid(ex)
////                } catch (ex: UnsupportedOperationException){
////                    log.error("Command ${command::class.java.simpleName} execution failed",ex)
////                    Result.Invalid(ex)
////                } catch (ex: Exception){
////                    log.error("Generic exception raised ${command::class.java.simpleName} execution failed",ex)
////                    Result.Invalid(ex)
////                }
//
//
////        is Result.Invalid ->{
////        log.error("Loading Aggregate ${command.aggregateID} failed", actualAggregateResult.err)
////        actualAggregateResult
//
//    override suspend fun handle(command: ICommand<A>): Result<A> = withContext(coroutineContext) {
//        handle(command) { mapOf<String, String>() }
//    }
//
//
//    protected abstract suspend fun onSuccess(aggregate: A): Result<Unit>
//}