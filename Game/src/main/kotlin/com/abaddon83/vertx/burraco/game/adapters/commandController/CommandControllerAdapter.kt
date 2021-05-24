package com.abaddon83.vertx.burraco.game.adapters.commandController

import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.utils.functionals.Invalid
import com.abaddon83.utils.functionals.Valid
import com.abaddon83.vertx.burraco.game.commands.*
import com.abaddon83.vertx.burraco.game.ports.CommandControllerPort
import com.abaddon83.vertx.burraco.game.ports.EventBrokerProducerPort
import com.abaddon83.vertx.burraco.game.ports.EventStorePort
import com.abaddon83.vertx.burraco.game.ports.Outcome


class CommandControllerAdapter(override val eventStore: EventStorePort, private val eventBrokerProducerPort: EventBrokerProducerPort<String, String>) : CommandControllerPort {

    val commandHandle: CommandHandler = CommandHandler(eventStore,eventBrokerProducerPort)

    override fun createNewBurracoGame(gameIdentity: GameIdentity): Outcome {
        val cmdResult = commandHandle.handle(CreateNewBurracoGameCmd(gameIdentity))
        return CmdResultAdapter.toOutcome(cmdResult = cmdResult)
    }

    override fun addPlayer(burracoGameIdentity: GameIdentity, playerIdentity: PlayerIdentity): Outcome {
        val cmdResult = commandHandle.handle(AddPlayerCmd(burracoGameIdentity, playerIdentity))
        return CmdResultAdapter.toOutcome(cmdResult = cmdResult)
    }

    override fun initGame(burracoGameIdentity: GameIdentity, playerIdentity: PlayerIdentity): Outcome {
        val cmdResult = commandHandle.handle(InitGameCmd(burracoGameIdentity))
        return when (cmdResult) {
            is Valid -> Valid(cmdResult.value)
            is Invalid -> cmdResult
        }
    }

    override fun startGame(burracoGameIdentity: GameIdentity): Outcome {
        val cmdResult = commandHandle.handle(StartGameCmd(burracoGameIdentity))
        return when (cmdResult) {
            is Valid -> Valid(cmdResult.value)
            is Invalid -> cmdResult
        }
    }

    override fun pickUpCardFromDeck(burracoGameIdentity: GameIdentity, playerIdentity: PlayerIdentity): Outcome {
        TODO("Not yet implemented")
    }

    override fun dropCardOnDiscardPile(
        burracoGameIdentity: GameIdentity,
        playerIdentity: PlayerIdentity,
        cardToDrop: Card
    ): Outcome {
        TODO("Not yet implemented")
    }
}

object CmdResultAdapter {
    fun toOutcome(cmdResult: CmdResult): Outcome {
        return when (cmdResult) {
            is Valid -> Valid(cmdResult.value)
            is Invalid -> cmdResult
        }
    }
}