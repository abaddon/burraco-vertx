package com.abaddon83.vertx.burraco.game.adapters.commandController

import com.abaddon83.utils.ddd.Event
import com.abaddon83.utils.functionals.Invalid
import com.abaddon83.utils.functionals.Valid
import com.abaddon83.vertx.burraco.game.adapters.eventStoreAdapter.vertx.EventStoreVertxAdapter
import com.abaddon83.vertx.burraco.game.commands.AddPlayerCmd
import com.abaddon83.vertx.burraco.game.commands.CmdResult
import com.abaddon83.vertx.burraco.game.commands.CreateNewBurracoGameCmd
import com.abaddon83.burraco.common.events.BurracoGameCreated
import com.abaddon83.burraco.common.events.BurracoGameEvent
import com.abaddon83.burraco.common.events.GameStarted
import com.abaddon83.burraco.common.events.PlayerAdded
import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.vertx.burraco.game.commands.StartGameCmd
import com.abaddon83.vertx.burraco.game.ports.CommandControllerPort
import com.abaddon83.vertx.burraco.game.ports.EventStorePort
import com.abaddon83.vertx.burraco.game.ports.Outcome
import io.vertx.core.Vertx


class CommandControllerAdapter(override val eventStore: EventStorePort) : CommandControllerPort {

    override fun createNewBurracoGame(gameIdentity: GameIdentity): Outcome {
        val cmdResult = commandHandle.handle(CreateNewBurracoGameCmd(gameIdentity))
        return CmdResultAdapter.toOutcome(cmdResult = cmdResult)
    }

    override fun addPlayer(burracoGameIdentity: GameIdentity, playerIdentity: PlayerIdentity): Outcome {
        val cmdResult = commandHandle.handle(AddPlayerCmd(burracoGameIdentity, playerIdentity))
        return CmdResultAdapter.toOutcome(cmdResult = cmdResult)
    }

    override fun startGame(burracoGameIdentity: GameIdentity, playerIdentity: PlayerIdentity): Outcome {
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