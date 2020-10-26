package com.abaddon83.vertx.burraco.engine.adapters.commandController

import com.abaddon83.utils.es.Event
import com.abaddon83.utils.functionals.Invalid
import com.abaddon83.utils.functionals.Valid
import com.abaddon83.vertx.burraco.engine.adapters.commandController.bodyRequests.CreateGameRequest
import com.abaddon83.vertx.burraco.engine.adapters.commandController.bodyRequests.JoinGameRequest
import com.abaddon83.vertx.burraco.engine.adapters.commandController.models.ErrorMsgModule
import com.abaddon83.vertx.burraco.engine.adapters.eventStoreAdapter.vertx.EventStoreVertxAdapter
import com.abaddon83.vertx.burraco.engine.commands.AddPlayerCmd
import com.abaddon83.vertx.burraco.engine.commands.CmdResult
import com.abaddon83.vertx.burraco.engine.commands.CreateNewBurracoGameCmd
import com.abaddon83.vertx.burraco.engine.events.BurracoGameCreated
import com.abaddon83.vertx.burraco.engine.events.GameStarted
import com.abaddon83.vertx.burraco.engine.events.PlayerAdded
import com.abaddon83.vertx.burraco.engine.models.decks.Card
import com.abaddon83.vertx.burraco.engine.models.games.GameIdentity
import com.abaddon83.vertx.burraco.engine.models.players.PlayerIdentity
import com.abaddon83.vertx.burraco.engine.ports.CommandControllerPort
import com.abaddon83.vertx.burraco.engine.ports.EventStorePort
import com.abaddon83.vertx.burraco.engine.ports.Outcome
import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.api.validation.ValidationException
import io.vertx.ext.web.handler.BodyHandler


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
        TODO("Not yet implemented")
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
            is Invalid -> Invalid(cmdResult.err)
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