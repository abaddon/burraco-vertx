package com.abaddon83.burraco.readModel.commands

import com.abaddon83.burraco.common.events.BurracoGameEvent
import com.abaddon83.burraco.common.events.CardAssignedToPlayer
import com.abaddon83.burraco.common.events.CardPickedFromDeck
import com.abaddon83.burraco.common.events.PlayerAdded
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.burraco.readModel.ports.RepositoryPort
import com.abaddon83.burraco.readModel.projections.BurracoGame
import com.abaddon83.utils.ddd.readModel.Projection
import com.abaddon83.utils.functionals.*
import com.abaddon83.utils.logs.WithLog
import io.vertx.core.Promise
import org.slf4j.LoggerFactory

typealias CmdResult = Validated<DomainError, DomainResult>

data class DomainResult(val result: Boolean)

data class CommandMsg(val command: Command, val response: CmdResult) // a command with a result

//io.vertx.kafka.client.serialization.JsonObjectSerializer
class CommandHandler(val repository: RepositoryPort) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun handle(cmd: Command): Promise<CmdResult> {
        val resultPromise: Promise<CmdResult> = Promise.promise()
        executeCommand(cmd).future()
            .onSuccess { result ->
                resultPromise.complete(result)
            }
        return resultPromise
    }

    private fun executeCommand(command: Command): Promise<CmdResult> {
        val resultPromise: Promise<CmdResult> = Promise.promise()
        val res = processPoly(command)
        res.future().onSuccess { cmdResult ->
            if (cmdResult is Valid<DomainResult>) {
                when(cmdResult.value.result){
                    true -> resultPromise.complete(cmdResult)
                    false -> resultPromise.fail("Event didn't apply")
                }
            }

        }

        return resultPromise
    }

    private fun processPoly(c: Command): Promise<CmdResult> {

        log.debug("Processing $c")

        val cmdResult = when (c) {
            is ApplyEventBurracoGameCreated -> execute(c)
            is ApplyEventPlayerAdded -> execute(c)
            is ApplyEventGameInitialised -> execute(c)
            is ApplyEventCardAssignedToPlayer -> execute(c)
            is ApplyEventCardAssignedToPlayerDeck -> execute(c)
            is ApplyEventCardAssignedToDeck -> execute(c)
            is ApplyEventCardAssignedToDiscardDeck -> execute(c)
            is ApplyEventGameStarted -> execute(c)
            is ApplyEventCardPickedFromDeck -> execute(c)
            else -> TODO()
        }
        return cmdResult
    }

    private fun execute(c: ApplyEventBurracoGameCreated): Promise<CmdResult> {
        val promiseResult = Promise.promise<CmdResult>()
        val event = c.event
        updateBurracoGame(event)
        promiseResult.complete(Valid(DomainResult(true)))

        return promiseResult
    }

    private fun execute(c: ApplyEventPlayerAdded): Promise<CmdResult> {
        val promiseResult = Promise.promise<CmdResult>()
        val event = c.event
        updateBurracoGame(event)
        updateGamePlayer(event.playerIdentity,event)
        promiseResult.complete(Valid(DomainResult(true)))

        return promiseResult
    }

    private fun execute(c: ApplyEventGameInitialised): Promise<CmdResult> {
        val promiseResult = Promise.promise<CmdResult>()
        val event = c.event
        updateBurracoGame(event)
        promiseResult.complete(Valid(DomainResult(true)))

        return promiseResult
    }

    private fun execute(c: ApplyEventCardAssignedToPlayer): Promise<CmdResult> {
        val promiseResult = Promise.promise<CmdResult>()
        val event = c.event
        updateBurracoGame(event)
        updateGamePlayer(event.player,event)
        promiseResult.complete(Valid(DomainResult(true)))

        return promiseResult
    }

    private fun execute(c: ApplyEventCardAssignedToPlayerDeck): Promise<CmdResult> {
        val promiseResult = Promise.promise<CmdResult>()
        val event = c.event
        updateBurracoGame(event)
        promiseResult.complete(Valid(DomainResult(true)))

        return promiseResult
    }

    private fun execute(c: ApplyEventCardAssignedToDeck): Promise<CmdResult> {
        val promiseResult = Promise.promise<CmdResult>()
        val event = c.event
        updateBurracoGame(event)
        promiseResult.complete(Valid(DomainResult(true)))

        return promiseResult
    }

    private fun execute(c: ApplyEventCardAssignedToDiscardDeck): Promise<CmdResult> {
        val promiseResult = Promise.promise<CmdResult>()
        val event = c.event
        updateBurracoGame(event)

        promiseResult.complete(Valid(DomainResult(true)))

        return promiseResult
    }

    private fun execute(c: ApplyEventGameStarted): Promise<CmdResult> {
        val promiseResult = Promise.promise<CmdResult>()
        val event = c.event
        updateBurracoGame(event)

        promiseResult.complete(Valid(DomainResult(true)))

        return promiseResult
    }

    private fun execute(c: ApplyEventCardPickedFromDeck): Promise<CmdResult> {
        val promiseResult = Promise.promise<CmdResult>()
        val event = c.event
        updateBurracoGame(event)
        updateGamePlayer(event.playerIdentity,event)
        promiseResult.complete(Valid(DomainResult(true)))

        return promiseResult
    }




    private  fun updateBurracoGame(event: BurracoGameEvent){
        val burracoGame = repository.findGame(event.identity)
        repository.persist(burracoGame.applyEvent(event))
    }

    private  fun updateGamePlayer(playerIdentity: PlayerIdentity, event: BurracoGameEvent){
        val gamePlayer = repository.findGamePlayer(playerIdentity,event.identity)
        repository.persist(gamePlayer.applyEvent(event))
    }

}