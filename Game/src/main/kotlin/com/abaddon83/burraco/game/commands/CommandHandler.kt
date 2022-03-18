//package com.abaddon83.burraco.game.commands
//
//import com.abaddon83.burraco.common.events.BurracoGameEvent
//import com.abaddon83.burraco.game.ports.EventStorePort
//import com.abaddon83.burraco.game.models.game.Game
//import com.abaddon83.burraco.game.models.burracoGameExecutions.BurracoGameExecutionTurnBeginning
//import com.abaddon83.burraco.game.models.burracoGameExecutions.BurracoGameExecutionTurnEnd
//import com.abaddon83.burraco.game.models.burracoGameExecutions.BurracoGameExecutionTurnExecution
//import com.abaddon83.burraco.common.models.identities.GameIdentity
//import com.abaddon83.utils.ddd.Event
//import com.abaddon83.utils.functionals.*
//import com.abaddon83.utils.logs.WithLog
//import com.abaddon83.burraco.game.models.Player
//import com.abaddon83.burraco.game.models.burracoGameendeds.BurracoGameEnded
//import com.abaddon83.burraco.game.models.game.GameWaitingDealer
//import com.abaddon83.burraco.game.ports.GameEventsBrokerProducerPort
//import io.vertx.core.Promise
//import java.util.*
//
//typealias CmdResult = Validated<DomainError, DomainResult>
//typealias EsScope = EventStorePort.() -> CmdResult
//
//data class DomainResult(val events: Iterable<Event>, val game: Game?)
//
//data class CommandMsg(val command: Command, val response: CmdResult) // a command with a result
//
//class CommandHandler(
//    val eventStore: EventStorePort,
//    private val gameEventsBrokerProducer: GameEventsBrokerProducerPort<String, String>
//) : WithLog("CommandHandler") {
//
//    private val TOPIC: String = "game"
//
//    fun handle(cmd: Command): Promise<CmdResult> {
//        val resultPromise: Promise<CmdResult> = Promise.promise()
//        CommandMsg(cmd, Valid(DomainResult(listOf(), null))).let { it ->
//            executeCommand(it).future()
//                .onSuccess { cmdMsg ->
//                    resultPromise.complete(cmdMsg.response)
//                }
//        }
//        return resultPromise
//    }
//
//    private fun executeCommand(msg: CommandMsg): Promise<CommandMsg> {
//        val resultPromise: Promise<CommandMsg> = Promise.promise()
//        val res = processPoly(msg.command)//(eventStore)
//        res.future().onSuccess { cmdResult ->
//            if (cmdResult is Valid) {
//                eventStore.save(cmdResult.value.events){ result ->
//                    if(result){
//                        publishEvent(cmdResult.value)
//                    }else{
//                        log.error("Events not published to the eventStore")
//                    }
//                }
//            }
//            resultPromise.complete(msg.copy(response = cmdResult))
//        }
//
//        return resultPromise
//    }
//
//    private fun processPoly(c: Command): Promise<CmdResult> {
//
//        log.debug("Processing $c")
//
//        val cmdResult = when (c) {
//            is CreateNewBurracoGameCmd -> execute(c)
//            is AddPlayerCmd -> execute(c)
//            is InitGameCmd -> execute(c)
//
//            is ApplyCardToPlayer -> execute(c)
//            is ApplyCardToPlayerDeck -> execute(c)
//            is ApplyCardToDiscardDeck -> execute(c)
//            is ApplyCardToDeck -> execute(c)
//
//            is StartGameCmd -> execute(c)
//
//            is PickUpACardFromDeckCmd -> execute(c)
//            is PickUpCardsFromDiscardPileCmd -> execute(c)
//            is DropTrisCmd -> execute(c)
//            is DropScaleCmd -> execute(c)
//            is PickUpMazzettoDeckCmd -> execute(c)
//            is AppendCardOnBurracoCmd -> execute(c)
//            is DropCardOnDiscardPileCmd -> execute(c)
//            is EndGameCmd -> execute(c)
//            is EndPlayerTurnCmd -> execute(c)
//            else -> TODO()
//        }
//        return cmdResult
//    }
//
//    private fun execute(c: CreateNewBurracoGameCmd): Promise<CmdResult> {
//        val resultPromise: Promise<CmdResult> = Promise.promise()
//        eventStore.getBurracoGameEvents(c.gameIdentity.convertTo().toString()).future()
//            .onFailure { resultPromise.fail(it.cause) }
//            .onSuccess { eventList ->
//                val burracoGame = eventList.fold()
//                when (burracoGame) {
//                    is EmptyBurracoGame -> {
//                        val burracoGame = Game.create(c.gameIdentity)
//                        resultPromise.complete(Valid(DomainResult(burracoGame.getUncommittedChanges(), burracoGame)))
//                    }
//                    else -> resultPromise.complete(
//                        Invalid(
//                            BurracoGameError(
//                                "BurracoGame ${c.gameIdentity} already exist",
//                                burracoGame
//                            )
//                        )
//                    )
//                }
//            }
//        return resultPromise
//    }
//
//    private fun execute(c: InitGameCmd): Promise<CmdResult> {
//        val resultPromise: Promise<CmdResult> = Promise.promise()
//        eventStore.getBurracoGameEvents(c.gameIdentity.convertTo().toString()).future()
//            .onFailure { resultPromise.fail(it.cause) }
//            .onSuccess { eventList ->
//                val burracoGame = eventList.fold()
//                when (burracoGame) {
//                    is BurracoGameWaitingPlayers ->
//                        resultPromise.complete(tryToExecute {
//                            val newBurracoGame = burracoGame.start();
//                            Valid(DomainResult(newBurracoGame.getUncommittedChanges(), newBurracoGame))
//                        })
//                    else -> resultPromise.complete(
//                        Invalid(
//                            BurracoGameError(
//                                "The game ${c.gameIdentity} doesn't exist or is in a different status, this command can not be executed",
//                                burracoGame
//                            )
//                        )
//                    )
//                }
//            }
//        return resultPromise
//    }
//
//    private fun execute(c: AddPlayerCmd): Promise<CmdResult> {
//        val resultPromise: Promise<CmdResult> = Promise.promise()
//        eventStore.getBurracoGameEvents(c.gameIdentity.convertTo().toString()).future()
//            .onFailure { resultPromise.fail(it.cause) }
//            .onSuccess { eventList ->
//                val burracoGame = eventList.fold()
//                when (burracoGame) {
//                    is BurracoGameWaitingPlayers -> {
//                        try {
//                            val newBurracoGame = burracoGame.addPlayer(Player(c.playerIdentityToAdd))
//                            resultPromise.complete(
//                                Valid(DomainResult(newBurracoGame.getUncommittedChanges(), newBurracoGame))
//                            )
//                        } catch (e: IllegalStateException) {
//                            resultPromise.complete(
//                                Invalid(BurracoGameError(e.message ?: "GameIdentity ${c.gameIdentity}. unknown error", burracoGame))
//                            )
//                        }
//                    }
//                    else -> resultPromise.complete(Invalid(BurracoGameError("GameIdentity ${c.gameIdentity} not found", burracoGame)))
//                }
//            }
//        return resultPromise
//    }
//
//    private fun execute(c: ApplyCardToPlayer): Promise<CmdResult> {
//        val resultPromise: Promise<CmdResult> = Promise.promise()
//        eventStore.getBurracoGameEvents(c.gameIdentity.convertTo().toString()).future()
//            .onFailure { resultPromise.fail(it.cause) }
//            .onSuccess { eventList ->
//                val burracoGame = eventList.fold()
//                when (burracoGame) {
//                    is GameWaitingDealer -> {
//                        try {
//                            val newBurracoGame = burracoGame.dealPlayerCard(c.playerIdentity, c.card)
//                            resultPromise.complete(Valid(DomainResult(newBurracoGame.getUncommittedChanges(), newBurracoGame)))
//                        } catch (e: IllegalStateException) {
//                            resultPromise.complete(Invalid(BurracoGameError(e.message ?: "GameIdentity ${c.gameIdentity}. unknown error", burracoGame)))
//
//                        }
//                    }
//                    else -> resultPromise.complete(Invalid(BurracoGameError("GameIdentity ${c.gameIdentity} not found", burracoGame)))
//                }
//            }
//        return resultPromise
//    }
//
//    private fun execute(c: ApplyCardToPlayerDeck): Promise<CmdResult> {
//        val resultPromise: Promise<CmdResult> = Promise.promise()
//        eventStore.getBurracoGameEvents(c.gameIdentity.convertTo().toString()).future()
//            .onFailure { resultPromise.fail(it.cause) }
//            .onSuccess { eventList ->
//                val burracoGame = eventList.fold()
//                when (burracoGame) {
//                    is GameWaitingDealer -> {
//                        try {
//                            val newBurracoGame = burracoGame.dealPlayerDeckCard(c.playerDeckId, c.card)
//                            resultPromise.complete(Valid(DomainResult(newBurracoGame.getUncommittedChanges(), newBurracoGame)))
//                        } catch (e: IllegalStateException) {
//                            resultPromise.complete(Invalid(BurracoGameError(e.message ?: "GameIdentity ${c.gameIdentity}. unknown error", burracoGame)))
//                        }
//                    }
//                    else -> resultPromise.complete(Invalid(BurracoGameError("GameIdentity ${c.gameIdentity} not found", burracoGame)))
//                }
//            }
//        return resultPromise
//    }
//
//    private fun execute(c: ApplyCardToDiscardDeck): Promise<CmdResult> {
//        val resultPromise: Promise<CmdResult> = Promise.promise()
//        eventStore.getBurracoGameEvents(c.gameIdentity.convertTo().toString()).future()
//            .onFailure { resultPromise.fail(it.cause) }
//            .onSuccess { eventList ->
//                val burracoGame = eventList.fold()
//                when (burracoGame) {
//                    is GameWaitingDealer -> {
//                        try {
//                            val newBurracoGame = burracoGame.dealDiscardDeckCard(c.card)
//                            resultPromise.complete(Valid(DomainResult(newBurracoGame.getUncommittedChanges(), newBurracoGame)))
//                        } catch (e: IllegalStateException) {
//                            resultPromise.complete(Invalid(BurracoGameError(e.message ?: "GameIdentity ${c.gameIdentity}. unknown error", burracoGame)))
//                        }
//                    }
//                    else -> resultPromise.complete(Invalid(BurracoGameError("GameIdentity ${c.gameIdentity} not found", burracoGame)))
//                }
//            }
//        return resultPromise
//    }
//
//    private fun execute(c: ApplyCardToDeck): Promise<CmdResult> {
//        val resultPromise: Promise<CmdResult> = Promise.promise()
//        eventStore.getBurracoGameEvents(c.gameIdentity.convertTo().toString()).future()
//            .onFailure { resultPromise.fail(it.cause) }
//            .onSuccess { eventList ->
//                val burracoGame = eventList.fold()
//                when (burracoGame) {
//                    is GameWaitingDealer -> {
//                        try {
//                            val newBurracoGame = burracoGame.dealDeckCard(c.card)
//                            resultPromise.complete(Valid(DomainResult(newBurracoGame.getUncommittedChanges(), newBurracoGame)))
//                        } catch (e: IllegalStateException) {
//                            resultPromise.complete(Invalid(
//                                BurracoGameError(
//                                    e.message ?: "GameIdentity ${c.gameIdentity}. unknown error",
//                                    burracoGame
//                                )
//                            ))
//                        }
//                    }
//                    else -> resultPromise.complete(Invalid(BurracoGameError("GameIdentity ${c.gameIdentity} not found", burracoGame)))
//                }
//            }
//        return resultPromise
//    }
//
//    private fun execute(c: StartGameCmd): Promise<CmdResult> {
//        val resultPromise: Promise<CmdResult> = Promise.promise()
//        eventStore.getBurracoGameEvents(c.gameIdentity.convertTo().toString()).future()
//            .onFailure { resultPromise.fail(it.cause) }
//            .onSuccess { eventList ->
//                val burracoGame = eventList.fold()
//                when (burracoGame) {
//                    is GameWaitingDealer -> {
//                        try {
//                            val newBurracoGame = burracoGame.startGame()
//                            resultPromise.complete(Valid(DomainResult(newBurracoGame.getUncommittedChanges(), newBurracoGame)))
//                        } catch (e: IllegalStateException) {
//                            resultPromise.complete(Invalid(
//                                BurracoGameError(
//                                    e.message ?: "GameIdentity ${c.gameIdentity}. unknown error",
//                                    burracoGame
//                                )
//                            ))
//                        }
//                    }
//                    else -> resultPromise.complete(Invalid(BurracoGameError("GameIdentity ${c.gameIdentity} not found", burracoGame)))
//                }
//            }
//        return resultPromise
//    }
//
//    private fun tryToExecute(block: () -> CmdResult): CmdResult {
//        return try {
//            block()
//        } catch (e: IllegalStateException) {
//            Invalid(BurracoGameError(e.message ?: "Unknown error in ${this::class.qualifiedName}"))
//        } catch (e: NoSuchElementException) {
//            Invalid(BurracoGameError(e.message ?: "Unknown error in ${this::class.qualifiedName}"))
//        }
//    }
//
//
//    private fun execute(c: PickUpACardFromDeckCmd): Promise<CmdResult> {
//        val resultPromise: Promise<CmdResult> = Promise.promise()
//        eventStore.getBurracoGameEvents(c.gameIdentity.convertTo().toString()).future()
//            .onFailure { resultPromise.fail(it.cause) }
//            .onSuccess { eventList ->
//                val burracoGame = eventList.fold()
//                when (burracoGame) {
//                    is BurracoGameExecutionTurnBeginning -> resultPromise.complete(
//                        tryToExecute {
//                        val newBurracoGame = burracoGame.pickUpACardFromDeck(playerIdentity = c.playerIdentity);
//                        Valid(DomainResult(newBurracoGame.getUncommittedChanges(), newBurracoGame))
//                    })
//                    else -> resultPromise.complete(Invalid(BurracoGameError("GameIdentity ${c.gameIdentity} not found", burracoGame)))
//                }
//            }
//        return resultPromise
//    }
//
//    private fun execute(c: PickUpCardsFromDiscardPileCmd): Promise<CmdResult> {
//        val resultPromise: Promise<CmdResult> = Promise.promise()
//        eventStore.getBurracoGameEvents(c.gameIdentity.convertTo().toString()).future()
//            .onFailure { resultPromise.fail(it.cause) }
//            .onSuccess { eventList ->
//                val burracoGame = eventList.fold()
//                when (burracoGame) {
//                    is BurracoGameExecutionTurnBeginning -> resultPromise.complete(tryToExecute {
//                        val newBurracoGame = burracoGame.pickUpCardsFromDiscardPile(playerIdentity = c.playerIdentity)
//                        Valid(DomainResult(newBurracoGame.getUncommittedChanges(), newBurracoGame))
//                    })
//                    else -> resultPromise.complete(Invalid(BurracoGameError("GameIdentity ${c.gameIdentity} not found", burracoGame)))
//                }
//            }
//        return resultPromise
//    }
//
//
//    private fun execute(c: DropTrisCmd): Promise<CmdResult> {
//        val resultPromise: Promise<CmdResult> = Promise.promise()
//        eventStore.getBurracoGameEvents(c.gameIdentity.convertTo().toString()).future()
//            .onFailure { resultPromise.fail(it.cause) }
//            .onSuccess { eventList ->
//                val burracoGame = eventList.fold()
//                when (burracoGame) {
//                    is BurracoGameExecutionTurnExecution -> resultPromise.complete(tryToExecute {
//                        val newBurracoGame =
//                            burracoGame.dropOnTableATris(playerIdentity = c.playerIdentity, tris = c.tris)
//                        Valid(DomainResult(newBurracoGame.getUncommittedChanges(), newBurracoGame))
//                    })
//                    else -> resultPromise.complete(Invalid(BurracoGameError("GameIdentity ${c.gameIdentity} not found", burracoGame)))
//                }
//            }
//        return resultPromise
//    }
//
//    private fun execute(c: DropScaleCmd): Promise<CmdResult> {
//        val resultPromise: Promise<CmdResult> = Promise.promise()
//        eventStore.getBurracoGameEvents(c.gameIdentity.convertTo().toString()).future()
//            .onFailure { resultPromise.fail(it.cause) }
//            .onSuccess { eventList ->
//                val burracoGame = eventList.fold()
//                when (burracoGame) {
//                    is BurracoGameExecutionTurnExecution -> resultPromise.complete(tryToExecute {
//                        val newBurracoGame =
//                            burracoGame.dropOnTableAScale(playerIdentity = c.playerIdentity, scale = c.scale)
//                        Valid(DomainResult(newBurracoGame.getUncommittedChanges(), newBurracoGame))
//                    })
//                    else -> resultPromise.complete(Invalid(BurracoGameError("GameIdentity ${c.gameIdentity} not found", burracoGame)))
//                }
//            }
//        return resultPromise
//    }
//
//    private fun execute(c: PickUpMazzettoDeckCmd): Promise<CmdResult> {
//        val resultPromise: Promise<CmdResult> = Promise.promise()
//        eventStore.getBurracoGameEvents(c.gameIdentity.convertTo().toString()).future()
//            .onFailure { resultPromise.fail(it.cause) }
//            .onSuccess { eventList ->
//                val burracoGame = eventList.fold()
//                when (burracoGame) {
//                    is BurracoGameExecutionTurnExecution -> resultPromise.complete(tryToExecute {
//                        val newBurracoGame = burracoGame.pickupMazzetto(playerIdentity = c.playerIdentity)
//                        Valid(DomainResult(newBurracoGame.getUncommittedChanges(), newBurracoGame))
//                    })
////                try {
////                    Valid(burracoGame.pickupMazzetto(playerIdentity = c.playerIdentity).getUncommittedChanges())
////                } catch (e: Exception) {
////                    Invalid(BurracoGameError(cmd = c, exception = e, burracoGame = burracoGame))
////                }
//                    is BurracoGameExecutionTurnEnd -> resultPromise.complete(tryToExecute {
//                        val newBurracoGame = burracoGame.pickupMazzetto(playerIdentity = c.playerIdentity)
//                        Valid(DomainResult(newBurracoGame.getUncommittedChanges(), newBurracoGame))
//                    })
////
////                try {
////                    Valid(burracoGame.pickupMazzetto(playerIdentity = c.playerIdentity).getUncommittedChanges())
////                } catch (e: Exception) {
////                    Invalid(BurracoGameError(cmd = c, exception = e, burracoGame = burracoGame))
////                }
//                    is BurracoGameExecutionTurnBeginning -> throw Exception("Not yet implemented the possibility to pickup mazzetto during the beginning phase")
//                    else -> resultPromise.complete(Invalid(BurracoGameError("GameIdentity ${c.gameIdentity} not found", burracoGame)))
//                }
//            }
//        return resultPromise
//    }
//
//
//    private fun execute(c: AppendCardOnBurracoCmd): Promise<CmdResult> {
//        val resultPromise: Promise<CmdResult> = Promise.promise()
//        eventStore.getBurracoGameEvents(c.gameIdentity.convertTo().toString()).future()
//            .onFailure { resultPromise.fail(it.cause) }
//            .onSuccess { eventList ->
//                val burracoGame = eventList.fold()
//                when (burracoGame) {
//                    is BurracoGameExecutionTurnExecution -> resultPromise.complete(tryToExecute {
//                        val newBurracoGame = burracoGame.appendCardsOnABurracoDropped(
//                            playerIdentity = c.playerIdentity,
//                            cardsToAppend = c.cardsToAppend,
//                            burracoIdentity = c.burracoIdentity
//                        )
//                        Valid(DomainResult(newBurracoGame.getUncommittedChanges(), newBurracoGame))
//                    })
////            } catch (e: Exception) {
////                Invalid(BurracoGameError(cmd = c, exception = e, burracoGame = burracoGame))
////            }
//                    else -> resultPromise.complete(Invalid(BurracoGameError("GameIdentity ${c.gameIdentity} not found", burracoGame)))
//                }
//            }
//        return resultPromise
//    }
//
//    private fun execute(c: DropCardOnDiscardPileCmd): Promise<CmdResult> {
//        val resultPromise: Promise<CmdResult> = Promise.promise()
//        eventStore.getBurracoGameEvents(c.gameIdentity.convertTo().toString()).future()
//            .onFailure { resultPromise.fail(it.cause) }
//            .onSuccess { eventList ->
//                val burracoGame = eventList.fold()
//                when (burracoGame) {
//                    is BurracoGameExecutionTurnExecution -> resultPromise.complete(tryToExecute {
//                        val newBurracoGame =
//                            burracoGame.dropCardOnDiscardPile(playerIdentity = c.playerIdentity, card = c.card)
//                        Valid(DomainResult(newBurracoGame.getUncommittedChanges(), newBurracoGame))
//                    })
//                    else -> resultPromise.complete(Invalid(BurracoGameError("GameIdentity ${c.gameIdentity} not found", burracoGame)))
//                }
//            }
//        return resultPromise
//    }
//
//    private fun execute(c: EndGameCmd): Promise<CmdResult> {
//        val resultPromise: Promise<CmdResult> = Promise.promise()
//        eventStore.getBurracoGameEvents(c.gameIdentity.convertTo().toString()).future()
//            .onFailure { resultPromise.fail(it.cause) }
//            .onSuccess { eventList ->
//                val burracoGame = eventList.fold()
//                when (burracoGame) {
//                    is BurracoGameExecutionTurnEnd -> resultPromise.complete(tryToExecute {
//                        val newBurracoGame = burracoGame.completeGame(playerIdentity = c.playerIdentity)
//                        Valid(DomainResult(newBurracoGame.getUncommittedChanges(), newBurracoGame))
//                    })
//                    else -> resultPromise.complete(Invalid(BurracoGameError("GameIdentity ${c.gameIdentity} not found", burracoGame)))
//                }
//            }
//        return resultPromise
//    }
//
//
//    private fun execute(c: EndPlayerTurnCmd): Promise<CmdResult> {
//        val resultPromise: Promise<CmdResult> = Promise.promise()
//        eventStore.getBurracoGameEvents(c.gameIdentity.convertTo().toString()).future()
//            .onFailure { resultPromise.fail(it.cause) }
//            .onSuccess { eventList ->
//                val burracoGame = eventList.fold()
//                when (burracoGame) {
//                    is BurracoGameExecutionTurnEnd -> resultPromise.complete(tryToExecute {
//                        val newBurracoGame = burracoGame.nextPlayerTurn(playerIdentity = c.playerIdentity)
//                        Valid(DomainResult(newBurracoGame.getUncommittedChanges(), newBurracoGame))
//                    })
//                    else -> resultPromise.complete(Invalid(BurracoGameError("GameIdentity ${c.gameIdentity} not found", burracoGame)))
//                }
//            }
//        return resultPromise
//    }
//
//    private fun List<BurracoGameEvent>.fold(): Game {
//        val emptyBurracoGame = EmptyBurracoGame(GameIdentity(UUID.fromString("00000000-0000-0000-0000-000000000000")))
//        return when (this.isEmpty()) {
//            true -> emptyBurracoGame
//            false -> this.fold(emptyBurracoGame) { i: Game, e: BurracoGameEvent ->
//                when (i) {
//                    is BurracoGameWaitingPlayers -> i.applyEvent(e)
//                    is BurracoGameExecutionTurnBeginning -> i.applyEvent(e)
//                    is BurracoGameExecutionTurnExecution -> i.applyEvent(e)
//                    is BurracoGameExecutionTurnEnd -> i.applyEvent(e)
//                    is BurracoGameEnded -> i.applyEvent(e)
//                    else -> i.applyEvent(e)
//                }
//            }
//        }
//    }
//
//    private fun publishEvent(domainResult: DomainResult) {
//        val key = domainResult.game?.identity()?.id.toString()
//        domainResult.events.forEach { event ->
//            gameEventsBrokerProducer.publish(gameEventsBrokerProducer.topic(), event)
////            val value =when(event){
////                is BurracoGameEvent -> event.toJson()
////                else -> null
////            }
////            if(value != null){
////                eventBrokerProducer.publish(TOPIC,event)
////            }
//        }
//
//
//    }
//}
//
//data class EmptyBurracoGame constructor(override val identity: GameIdentity) : Game(identity)