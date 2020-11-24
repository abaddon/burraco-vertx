package com.abaddon83.vertx.burraco.engine.adapters.commandController

import com.abaddon83.utils.ddd.Event
import com.abaddon83.utils.functionals.Invalid
import com.abaddon83.utils.functionals.Valid
import com.abaddon83.vertx.burraco.engine.adapters.eventStoreAdapter.vertx.EventStoreVertxAdapter
import com.abaddon83.vertx.burraco.engine.commands.AddPlayerCmd
import com.abaddon83.vertx.burraco.engine.commands.CmdResult
import com.abaddon83.vertx.burraco.engine.commands.CreateNewBurracoGameCmd
import com.abaddon83.burraco.common.events.BurracoGameCreated
import com.abaddon83.burraco.common.events.GameStarted
import com.abaddon83.burraco.common.events.PlayerAdded
import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.vertx.burraco.engine.commands.StartGameCmd
import com.abaddon83.vertx.burraco.engine.ports.CommandControllerPort
import com.abaddon83.vertx.burraco.engine.ports.EventStorePort
import com.abaddon83.vertx.burraco.engine.ports.Outcome
import io.vertx.core.Vertx


class CommandControllerAdapter(vertx: Vertx) : CommandControllerPort {

    private val vertx: Vertx = vertx
    override val eventStore: EventStorePort
        get() = EventStoreVertxAdapter(vertx);

    override fun createNewBurracoGame(gameIdentity: GameIdentity): Outcome {
        val cmdResult = commandHandle.handle(CreateNewBurracoGameCmd(gameIdentity))
        return CmdResultAdapter.toOutcome(cmdResult = cmdResult)
    }

    override fun joinPlayer(burracoGameIdentity: GameIdentity, playerIdentity: PlayerIdentity): Outcome {
        val cmdResult = commandHandle.handle(AddPlayerCmd(burracoGameIdentity, playerIdentity))
        return CmdResultAdapter.toOutcome(cmdResult = cmdResult)
    }

    override fun startGame(burracoGameIdentity: GameIdentity, playerIdentity: PlayerIdentity): Outcome {
        val cmdResult = commandHandle.handle(StartGameCmd(burracoGameIdentity))
        return when (cmdResult) {
            is Valid -> Valid(
                mapOf(
                    "gameIdentity" to burracoGameIdentity.convertTo().toString(),
                    "status" to "started"
                )
            )
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
            is Valid -> Valid(convertEvent(cmdResult.value))
            is Invalid -> cmdResult
        }
    }

    private fun convertEvent(events: Iterable<Event>): Map<String, String> {
        return when (val event = events.first()) {
            is BurracoGameCreated -> mapOf("gameIdentity" to event.key())
            is PlayerAdded -> mapOf(
                "gameIdentity" to event.key(),
                "playerIdentity" to event.playerIdentity.convertTo().toString()
            )
            is GameStarted -> mapOf("gameIdentity" to event.key())
            else -> mapOf("msg" to "Ops.. ${event.javaClass.simpleName} event conversion is missing")
        }
    }
}