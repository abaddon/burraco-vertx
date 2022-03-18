//package com.abaddon83.burraco.game.adapters.commandController
//
//import com.abaddon83.burraco.common.models.identities.GameIdentity
//import com.abaddon83.burraco.common.models.identities.PlayerIdentity
//import com.abaddon83.burraco.common.models.valueObjects.Card
//import com.abaddon83.utils.functionals.Invalid
//import com.abaddon83.utils.functionals.Valid
//import com.abaddon83.burraco.game.commands.*
//import com.abaddon83.burraco.game.ports.CommandControllerPort
//import com.abaddon83.burraco.game.ports.GameEventsBrokerProducerPort
//import com.abaddon83.burraco.game.ports.EventStorePort
//import com.abaddon83.burraco.game.ports.Outcome
//import io.vertx.core.Promise
//
//
//class CommandControllerAdapter(override val eventStore: EventStorePort, private val gameEventsBrokerProducer: GameEventsBrokerProducerPort<String, String>) : CommandControllerPort {
//
//    private val commandHandle: CommandHandler = CommandHandler(eventStore,gameEventsBrokerProducer)
//
//    override fun createNewBurracoGame(gameIdentity: GameIdentity): Promise<Outcome> {
//        val cmdResult = commandHandle.handle(CreateNewBurracoGameCmd(gameIdentity))
//        return CmdResultAdapter.toOutcome(cmdResult = cmdResult)
//    }
//
//    override fun addPlayer(burracoGameIdentity: GameIdentity, playerIdentity: PlayerIdentity): Promise<Outcome> {
//        val cmdResult = commandHandle.handle(AddPlayerCmd(burracoGameIdentity, playerIdentity))
//        return CmdResultAdapter.toOutcome(cmdResult = cmdResult)
//    }
//
//    override fun initGame(burracoGameIdentity: GameIdentity, playerIdentity: PlayerIdentity): Promise<Outcome> {
//        val cmdResult = commandHandle.handle(InitGameCmd(burracoGameIdentity))
//        return CmdResultAdapter.toOutcome(cmdResult = cmdResult)
//    }
//
//    override fun startGame(burracoGameIdentity: GameIdentity): Promise<Outcome> {
//        val cmdResult = commandHandle.handle(StartGameCmd(burracoGameIdentity))
//        return CmdResultAdapter.toOutcome(cmdResult = cmdResult)
//    }
//
//    override fun pickUpCardFromDeck(burracoGameIdentity: GameIdentity, playerIdentity: PlayerIdentity): Promise<Outcome> {
//        TODO("Not yet implemented")
//    }
//
//    override fun dropCardOnDiscardPile(
//        burracoGameIdentity: GameIdentity,
//        playerIdentity: PlayerIdentity,
//        cardToDrop: Card
//    ): Promise<Outcome> {
//        TODO("Not yet implemented")
//    }
//}
//
//object CmdResultAdapter {
//    fun toOutcome(cmdResult: Promise<CmdResult>): Promise<Outcome> {
//        val promiseResult = Promise.promise<Outcome>()
//        cmdResult.future().onSuccess { result ->
//            when (result) {
//                is Valid -> promiseResult.complete(Valid(result.value))
//                is Invalid -> promiseResult.complete(result)
//            }
//        }
//        return promiseResult
//    }
//}