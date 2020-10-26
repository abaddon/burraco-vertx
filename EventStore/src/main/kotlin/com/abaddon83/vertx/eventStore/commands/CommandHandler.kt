package com.abaddon83.vertx.eventStore.commands

import com.abaddon83.utils.functionals.*
import com.abaddon83.vertx.eventStore.models.Event
import com.abaddon83.vertx.eventStore.ports.EventStreamPort
import com.abaddon83.vertx.eventStore.ports.OutcomeDetail
import com.abaddon83.vertx.eventStore.ports.RepositoryPort
import io.vertx.core.logging.LoggerFactory

typealias CmdResult = Validated<EventError, OutcomeDetail>


data class CommandMsg(val command: Command, val response: CmdResult) // a command with a result


class CommandHandler(
    private val repository: RepositoryPort,
    private val eventStream: EventStreamPort
){

    companion object{
        private val log = LoggerFactory.getLogger(this::class.qualifiedName)
    }

    fun handle(cmd: Command): CmdResult =
            CommandMsg(cmd, Valid(mapOf())).let {
                executeCommand(it).response
            }

    private fun executeCommand(msg: CommandMsg): CommandMsg {

        val res = processPoly(msg.command)
        return msg.copy(response = res)
    }

    private fun processPoly(c: Command): CmdResult {

        return when (c) {
            is PersistEventCmd -> execute(c)
            is PublishEventCmd -> execute(c)
            is PersistAndPublishEventCmd -> execute(c)
            //add the other commands to execute here
            else -> TODO()
        }
    }

    private fun execute(c: PersistEventCmd): CmdResult {
        val eventToSave = c.event
        return repository.save(eventToSave)
    }

    private fun execute(c: PublishEventCmd): CmdResult {
        val eventTopPublish = c.event
        return eventStream.publish(eventTopPublish)
    }

    private fun execute(c: PersistAndPublishEventCmd): CmdResult {
        val persistEventCmd = PersistEventCmd(c.event)
        val publishEventCmd = PublishEventCmd(c.event)
        return when (val r1=handle(persistEventCmd)){
            is Invalid -> r1
            is Valid ->  when (val r2=handle(publishEventCmd)){
                is Invalid -> Invalid(EventError("Event saved but not published!"))
                is Valid ->   r1.copy(value = r1.value.plus(r2.value))
            }
        }

    }
}