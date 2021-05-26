package com.abaddon83.vertx.burraco.game.commands

import com.abaddon83.burraco.common.events.BurracoGameEvent
import com.abaddon83.vertx.burraco.game.ports.EventStorePort
import com.abaddon83.vertx.burraco.game.models.BurracoGame
import com.abaddon83.vertx.burraco.game.models.burracoGameExecutions.BurracoGameExecutionTurnBeginning
import com.abaddon83.vertx.burraco.game.models.burracoGameExecutions.BurracoGameExecutionTurnEnd
import com.abaddon83.vertx.burraco.game.models.burracoGameExecutions.BurracoGameExecutionTurnExecution
import com.abaddon83.vertx.burraco.game.models.burracoGameWaitingPlayers.BurracoGameWaitingPlayers
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.utils.ddd.Event
import com.abaddon83.utils.functionals.*
import com.abaddon83.utils.logs.WithLog
import com.abaddon83.vertx.burraco.game.models.BurracoPlayer
import com.abaddon83.vertx.burraco.game.models.burracoGameendeds.BurracoGameEnded
import com.abaddon83.vertx.burraco.game.models.burracoWaitingDealer.BurracoGameWaitingDealer
import com.abaddon83.vertx.burraco.game.ports.EventBrokerProducerPort
import jdk.nashorn.internal.objects.Global
import java.util.*

typealias CmdResult = Validated<DomainError, DomainResult>
typealias EsScope = EventStorePort.() -> CmdResult

data class DomainResult(val events: Iterable<Event>, val game: BurracoGame?)

data class CommandMsg(val command: Command, val response: CmdResult) // a command with a result

class CommandHandler(val eventStore: EventStorePort, private val eventBrokerProducer: EventBrokerProducerPort<String,String>) : WithLog("CommandHandler") {

    private val TOPIC: String = "game"

    fun handle(cmd: Command): CmdResult =
        CommandMsg(cmd, Valid(DomainResult(listOf(),null))).let {
            executeCommand(it).response
        }

    private fun executeCommand(msg: CommandMsg): CommandMsg {
        val res = processPoly(msg.command)(eventStore)
        if (res is Valid) {
            eventStore.save(res.value.events)
            publishEvent(res.value)
        }
        return msg.copy(response = res)
    }

    private suspend fun processPoly(c: Command): EsScope {

        log.debug("Processing $c")

        val cmdResult = when (c) {
            is CreateNewBurracoGameCmd -> execute(c)
            is AddPlayerCmd -> execute(c)
            is InitGameCmd -> execute(c)

            is ApplyCardToPlayer -> execute(c)
            is ApplyCardToPlayerDeck -> execute(c)
            is ApplyCardToDiscardDeck -> execute(c)
            is ApplyCardToDeck -> execute(c)

            is StartGameCmd -> execute(c)

            is PickUpACardFromDeckCmd -> execute(c)
            is PickUpCardsFromDiscardPileCmd -> execute(c)
            is DropTrisCmd -> execute(c)
            is DropScaleCmd -> execute(c)
            is PickUpMazzettoDeckCmd -> execute(c)
            is AppendCardOnBurracoCmd -> execute(c)
            is DropCardOnDiscardPileCmd -> execute(c)
            is EndGameCmd -> execute(c)
            is EndPlayerTurnCmd -> execute(c)
            else -> TODO()
        }
        return cmdResult
    }

    private fun execute(c: CreateNewBurracoGameCmd): EsScope = {

        val burracoGame = getEvents<BurracoGameEvent>(c.gameIdentity.convertTo().toString()).fold()
        when (burracoGame) {
            is EmptyBurracoGame -> {
                val burracoGame = BurracoGame.create(c.gameIdentity)
                Valid(DomainResult(burracoGame.getUncommittedChanges(),burracoGame))
            }
            else -> Invalid(BurracoGameError("BurracoGame ${c.gameIdentity} already exist", burracoGame))
        }
    }

    private fun execute(c: InitGameCmd): EsScope = {
        val burracoGame = getEvents<BurracoGameEvent>(c.gameIdentity.convertTo().toString()).fold()
        when (burracoGame) {
            is BurracoGameWaitingPlayers -> tryToExecute {
                val newBurracoGame = burracoGame.start();
                Valid(DomainResult(newBurracoGame.getUncommittedChanges(),newBurracoGame))
            }
            else -> Invalid(
                BurracoGameError(
                    "The game ${c.gameIdentity} doesn't exist or is in a different status, this command can not be executed",
                    burracoGame
                )
            )
        }
    }

    private fun execute(c: AddPlayerCmd): EsScope = {
        when (val burracoGame = getEvents<BurracoGameEvent>(c.gameIdentity.convertTo().toString()).fold()) {
            is BurracoGameWaitingPlayers -> {
                try {
                    val newBurracoGame = burracoGame.addPlayer(BurracoPlayer(c.playerIdentityToAdd))
                    Valid(DomainResult(newBurracoGame.getUncommittedChanges(),newBurracoGame))
                } catch (e: IllegalStateException) {
                    Invalid(BurracoGameError(e.message ?: "GameIdentity ${c.gameIdentity}. unknown error", burracoGame))
                }
            }
            else -> Invalid(BurracoGameError("GameIdentity ${c.gameIdentity} not found", burracoGame))
        }
    }

    private fun execute(c: ApplyCardToPlayer): EsScope = {
        when (val burracoGame = getEvents<BurracoGameEvent>(c.gameIdentity.convertTo().toString()).fold()) {
            is BurracoGameWaitingDealer -> {
                try {
                    val newBurracoGame = burracoGame.dealPlayerCard(c.playerIdentity, c.card)
                    Valid(DomainResult(newBurracoGame.getUncommittedChanges(),newBurracoGame))
                } catch (e: IllegalStateException) {
                    Invalid(BurracoGameError(e.message ?: "GameIdentity ${c.gameIdentity}. unknown error", burracoGame))
                }
            }
            else -> Invalid(BurracoGameError("GameIdentity ${c.gameIdentity} not found", burracoGame))
        }
    }

    private fun execute(c: ApplyCardToPlayerDeck): EsScope = {
        when (val burracoGame = getEvents<BurracoGameEvent>(c.gameIdentity.convertTo().toString()).fold()) {
            is BurracoGameWaitingDealer -> {
                try {
                    val newBurracoGame = burracoGame.dealPlayerDeckCard(c.playerDeckId, c.card)
                    Valid(DomainResult(newBurracoGame.getUncommittedChanges(),newBurracoGame))
                } catch (e: IllegalStateException) {
                    Invalid(BurracoGameError(e.message ?: "GameIdentity ${c.gameIdentity}. unknown error", burracoGame))
                }
            }
            else -> Invalid(BurracoGameError("GameIdentity ${c.gameIdentity} not found", burracoGame))
        }
    }

    private fun execute(c: ApplyCardToDiscardDeck): EsScope = {
        when (val burracoGame = getEvents<BurracoGameEvent>(c.gameIdentity.convertTo().toString()).fold()) {
            is BurracoGameWaitingDealer -> {
                try {
                    val newBurracoGame = burracoGame.dealDiscardDeckCard(c.card)
                    Valid(DomainResult(newBurracoGame.getUncommittedChanges(),newBurracoGame))
                } catch (e: IllegalStateException) {
                    Invalid(BurracoGameError(e.message ?: "GameIdentity ${c.gameIdentity}. unknown error", burracoGame))
                }
            }
            else -> Invalid(BurracoGameError("GameIdentity ${c.gameIdentity} not found", burracoGame))
        }
    }

    private fun execute(c: ApplyCardToDeck): EsScope = {
        when (val burracoGame = getEvents<BurracoGameEvent>(c.gameIdentity.convertTo().toString()).fold()) {
            is BurracoGameWaitingDealer -> {
                try {
                    val newBurracoGame = burracoGame.dealDeckCard(c.card)
                    Valid(DomainResult(newBurracoGame.getUncommittedChanges(),newBurracoGame))
                } catch (e: IllegalStateException) {
                    Invalid(BurracoGameError(e.message ?: "GameIdentity ${c.gameIdentity}. unknown error", burracoGame))
                }
            }
            else -> Invalid(BurracoGameError("GameIdentity ${c.gameIdentity} not found", burracoGame))
        }
    }

    private fun execute(c: StartGameCmd): EsScope = {
        when (val burracoGame = getEvents<BurracoGameEvent>(c.gameIdentity.convertTo().toString()).fold()) {
            is BurracoGameWaitingDealer -> {
                try {
                    val newBurracoGame = burracoGame.startGame()
                    Valid(DomainResult(newBurracoGame.getUncommittedChanges(),newBurracoGame))
                } catch (e: IllegalStateException) {
                    Invalid(BurracoGameError(e.message ?: "GameIdentity ${c.gameIdentity}. unknown error", burracoGame))
                }
            }
            else -> Invalid(BurracoGameError("GameIdentity ${c.gameIdentity} not found", burracoGame))
        }
    }

    private fun tryToExecute(block: () -> CmdResult): CmdResult {
        return try {
            block()
        } catch (e: IllegalStateException) {
            Invalid(BurracoGameError(e.message ?: "Unknown error in ${this::class.qualifiedName}"))
        } catch (e: NoSuchElementException) {
            Invalid(BurracoGameError(e.message ?: "Unknown error in ${this::class.qualifiedName}"))
        }
    }



    private fun execute(c: PickUpACardFromDeckCmd): EsScope = {
        val burracoGame = getEvents<BurracoGameEvent>(c.gameIdentity.convertTo().toString()).fold()
        when (burracoGame) {
            is BurracoGameExecutionTurnBeginning -> tryToExecute {
                val newBurracoGame = burracoGame.pickUpACardFromDeck(playerIdentity = c.playerIdentity);
                Valid(DomainResult(newBurracoGame.getUncommittedChanges(),newBurracoGame))
            }
            else -> Invalid(BurracoGameError("GameIdentity ${c.gameIdentity} not found", burracoGame))
        }
    }

    private fun execute(c: PickUpCardsFromDiscardPileCmd): EsScope = {
        val burracoGame = getEvents<BurracoGameEvent>(c.gameIdentity.convertTo().toString()).fold()
        when (burracoGame) {
            is BurracoGameExecutionTurnBeginning -> tryToExecute {
                val newBurracoGame = burracoGame.pickUpCardsFromDiscardPile(playerIdentity = c.playerIdentity)
                Valid(DomainResult(newBurracoGame.getUncommittedChanges(),newBurracoGame))
            }
            else -> Invalid(BurracoGameError("GameIdentity ${c.gameIdentity} not found", burracoGame))
        }
    }


    private fun execute(c: DropTrisCmd): EsScope = {
        val burracoGame = getEvents<BurracoGameEvent>(c.gameIdentity.convertTo().toString()).fold()
        when (burracoGame) {
            is BurracoGameExecutionTurnExecution -> tryToExecute {
                val newBurracoGame = burracoGame.dropOnTableATris(playerIdentity = c.playerIdentity, tris = c.tris)
                Valid(DomainResult(newBurracoGame.getUncommittedChanges(),newBurracoGame))
            }
            else -> Invalid(BurracoGameError("GameIdentity ${c.gameIdentity} not found", burracoGame))
        }
    }

    private fun execute(c: DropScaleCmd): EsScope = {
        val burracoGame = getEvents<BurracoGameEvent>(c.gameIdentity.convertTo().toString()).fold()
        when (burracoGame) {
            is BurracoGameExecutionTurnExecution -> tryToExecute {
                val newBurracoGame = burracoGame.dropOnTableAScale(playerIdentity = c.playerIdentity, scale = c.scale)
                Valid(DomainResult(newBurracoGame.getUncommittedChanges(),newBurracoGame))
            }
            else -> Invalid(BurracoGameError("GameIdentity ${c.gameIdentity} not found", burracoGame))
        }
    }

    private fun execute(c: PickUpMazzettoDeckCmd): EsScope = {
        val burracoGame = getEvents<BurracoGameEvent>(c.gameIdentity.convertTo().toString()).fold()
        when (burracoGame) {
            is BurracoGameExecutionTurnExecution -> tryToExecute {
                val newBurracoGame = burracoGame.pickupMazzetto(playerIdentity = c.playerIdentity)
                Valid(DomainResult(newBurracoGame.getUncommittedChanges(),newBurracoGame))
            }
//                try {
//                    Valid(burracoGame.pickupMazzetto(playerIdentity = c.playerIdentity).getUncommittedChanges())
//                } catch (e: Exception) {
//                    Invalid(BurracoGameError(cmd = c, exception = e, burracoGame = burracoGame))
//                }
            is BurracoGameExecutionTurnEnd -> tryToExecute {
                val newBurracoGame = burracoGame.pickupMazzetto(playerIdentity = c.playerIdentity)
                Valid(DomainResult(newBurracoGame.getUncommittedChanges(),newBurracoGame))
            }
//
//                try {
//                    Valid(burracoGame.pickupMazzetto(playerIdentity = c.playerIdentity).getUncommittedChanges())
//                } catch (e: Exception) {
//                    Invalid(BurracoGameError(cmd = c, exception = e, burracoGame = burracoGame))
//                }
            is BurracoGameExecutionTurnBeginning -> throw Exception("Not yet implemented the possibility to pickup mazzetto during the beginning phase")
            else -> Invalid(BurracoGameError("GameIdentity ${c.gameIdentity} not found", burracoGame))
        }
    }


    private fun execute(c: AppendCardOnBurracoCmd): EsScope = {
        val burracoGame = getEvents<BurracoGameEvent>(c.gameIdentity.convertTo().toString()).fold()
        when (burracoGame) {
            is BurracoGameExecutionTurnExecution -> tryToExecute {
                val newBurracoGame = burracoGame.appendCardsOnABurracoDropped(
                    playerIdentity = c.playerIdentity,
                    cardsToAppend = c.cardsToAppend,
                    burracoIdentity = c.burracoIdentity)
                Valid(DomainResult(newBurracoGame.getUncommittedChanges(),newBurracoGame))
            }
//            } catch (e: Exception) {
//                Invalid(BurracoGameError(cmd = c, exception = e, burracoGame = burracoGame))
//            }
            else -> Invalid(BurracoGameError("GameIdentity ${c.gameIdentity} not found", burracoGame))
        }
    }

    private fun execute(c: DropCardOnDiscardPileCmd): EsScope = {
        val burracoGame = getEvents<BurracoGameEvent>(c.gameIdentity.convertTo().toString()).fold()
        when (burracoGame) {
            is BurracoGameExecutionTurnExecution -> tryToExecute {
                val newBurracoGame = burracoGame.dropCardOnDiscardPile(playerIdentity = c.playerIdentity, card = c.card)
                Valid(DomainResult(newBurracoGame.getUncommittedChanges(),newBurracoGame))
            }
            else -> Invalid(BurracoGameError("GameIdentity ${c.gameIdentity} not found", burracoGame))
        }
    }

    private fun execute(c: EndGameCmd): EsScope = {
        val burracoGame = getEvents<BurracoGameEvent>(c.gameIdentity.convertTo().toString()).fold()
        when (burracoGame) {
            is BurracoGameExecutionTurnEnd -> tryToExecute {
                val newBurracoGame = burracoGame.completeGame(playerIdentity = c.playerIdentity)
                Valid(DomainResult(newBurracoGame.getUncommittedChanges(),newBurracoGame))
            }
            else -> Invalid(BurracoGameError("GameIdentity ${c.gameIdentity} not found", burracoGame))
        }
    }


    private fun execute(c: EndPlayerTurnCmd): EsScope = {
        val burracoGame = getEvents<BurracoGameEvent>(c.gameIdentity.convertTo().toString()).fold()
        when (burracoGame) {
            is BurracoGameExecutionTurnEnd -> tryToExecute {
                val newBurracoGame = burracoGame.nextPlayerTurn(playerIdentity = c.playerIdentity)
                Valid(DomainResult(newBurracoGame.getUncommittedChanges(),newBurracoGame))
            }
            else -> Invalid(BurracoGameError("GameIdentity ${c.gameIdentity} not found", burracoGame))
        }
    }

    private fun List<BurracoGameEvent>.fold(): BurracoGame {
        val emptyBurracoGame = EmptyBurracoGame(GameIdentity(UUID.fromString("00000000-0000-0000-0000-000000000000")))
        return when (this.isEmpty()) {
            true -> emptyBurracoGame
            false -> this.fold(emptyBurracoGame) { i: BurracoGame, e: BurracoGameEvent ->
                when (i) {
                    is BurracoGameWaitingPlayers -> i.applyEvent(e)
                    is BurracoGameExecutionTurnBeginning -> i.applyEvent(e)
                    is BurracoGameExecutionTurnExecution -> i.applyEvent(e)
                    is BurracoGameExecutionTurnEnd -> i.applyEvent(e)
                    is BurracoGameEnded -> i.applyEvent(e)
                    else -> i.applyEvent(e)
                }
            }
        }
    }

    private fun publishEvent( domainResult : DomainResult){
        val key = domainResult.game?.identity()?.id.toString()
        domainResult.events.forEach{event ->
            eventBrokerProducer.publish(TOPIC,event)
//            val value =when(event){
//                is BurracoGameEvent -> event.toJson()
//                else -> null
//            }
//            if(value != null){
//                eventBrokerProducer.publish(TOPIC,event)
//            }
        }



    }
}

data class EmptyBurracoGame constructor(override val identity: GameIdentity) : BurracoGame(identity)