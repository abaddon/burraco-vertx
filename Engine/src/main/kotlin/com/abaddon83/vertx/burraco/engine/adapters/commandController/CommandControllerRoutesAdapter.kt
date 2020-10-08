package com.abaddon83.vertx.burraco.engine.adapters.commandController

import com.abaddon83.utils.es.Event
import com.abaddon83.utils.functionals.Invalid
import com.abaddon83.utils.functionals.Valid
import com.abaddon83.vertx.burraco.engine.adapters.commandController.models.ErrorMsgModule
import com.abaddon83.vertx.burraco.engine.adapters.eventStoreVertx.EventStoreVertxAdapter
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
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.kotlin.coroutines.awaitResult
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CommandControllerRoutesAdapter(vertx: Vertx) : CommandControllerPort {

    private val vertx: Vertx = vertx

    override val eventStore: EventStorePort
        get() = EventStoreVertxAdapter(vertx);

    fun getRouters(): Router {
        val router: Router = Router.router(vertx)
        router.route().handler(BodyHandler.create())

        router.post("/games/burraco").handler(::createNewBurracoGameHandler)

        return router
    }

    private fun createNewBurracoGameHandler(routingContext: RoutingContext) {

        val requestJson = routingContext.bodyAsJson

        GlobalScope.launch(vertx.dispatcher()) {
            try {
                var result = awaitResult<Outcome> { resultHandler ->
                    val outcome: Outcome = when (requestJson) {
                        null -> Invalid<String>("json missing") as Outcome
                        else -> {
                            when (requestJson.getString("gameType")) {
                                "BURRACO" -> createNewBurracoGame(GameIdentity.create())
                                null -> Invalid<String>("GameType field is missing") as Outcome
                                else -> Invalid<String>("GameType wrong") as Outcome
                            }
                        }
                    }
                    resultHandler.handle(Future.succeededFuture(outcome))
                }

                when (result) {
                    is Valid -> routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .setStatusCode(200)
                        .end(Json.encodePrettily(result))
                    is Invalid -> routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .setStatusCode(400)
                        .end(Json.encodePrettily(ErrorMsgModule(code = "400", message = result.err.toString())))
                }
            }catch (e: Exception) {
                routingContext.response().putHeader("Content-Type", "text/plain")
                val msg=StringBuilder()
                    .appendLine(e.message)
                    .appendLine(e.cause)
                    .appendLine(e.stackTrace.map { st -> "${st.fileName} ${st.methodName} ${st.lineNumber}" })
                    .toString()
                routingContext.response().end(msg)
            }
        }


    }

    override fun createNewBurracoGame(gameIdentity: GameIdentity): Outcome {
        val cmdResult = commandHandle.handle(CreateNewBurracoGameCmd(gameIdentity))
        return CmdResultAdapter.toOutcome(cmdResult = cmdResult)
    }

    override fun joinPlayer(burracoGameIdentity: GameIdentity, playerIdentity: PlayerIdentity): Outcome {
        TODO("Not yet implemented")
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