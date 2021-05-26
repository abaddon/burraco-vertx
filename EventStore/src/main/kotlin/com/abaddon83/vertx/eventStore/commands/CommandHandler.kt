package com.abaddon83.vertx.eventStore.commands

import com.abaddon83.utils.functionals.*
import com.abaddon83.vertx.eventStore.ports.EventStreamPort
import com.abaddon83.vertx.eventStore.ports.OutcomeDetail
import com.abaddon83.vertx.eventStore.ports.RepositoryPort
import org.slf4j.LoggerFactory

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
        eventStream.publish(eventTopPublish)
        return Valid(mapOf("msg" to "event pushed"))
    }
}