package com.abaddon83.vertx.burraco.engine.adapters.commandController

import com.abaddon83.utils.functionals.Invalid
import com.abaddon83.utils.functionals.Valid
import com.abaddon83.vertx.burraco.engine.adapters.commandController.bodyRequests.CreateGameRequest
import com.abaddon83.vertx.burraco.engine.adapters.commandController.bodyRequests.JoinGameRequest
import com.abaddon83.vertx.burraco.engine.adapters.commandController.models.ErrorMsgModule
import com.abaddon83.vertx.burraco.engine.adapters.eventStoreAdapter.vertx.EventStoreVertxAdapter
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


class CommandControllerRoutes(vertx: Vertx) {

    private val vertx: Vertx = vertx

    private val eventStore: EventStorePort
        get() = EventStoreVertxAdapter(vertx);

    private val controllerAdapter: CommandControllerPort
        get() = CommandControllerAdapter(vertx)

    fun getRouters(): Router {
        val router: Router = Router.router(vertx)
        router.route().handler(BodyHandler.create())

        router.post("/games/burraco").handler(CreateGameRequest.getValidator()).handler(::createNewBurracoGameHandler)
            .failureHandler(::failureHandler)
        router.post("/games/burraco/:gameId/join").handler(JoinGameRequest.getValidator()).handler(::joinPlayerHandler)
            .failureHandler(::failureHandler)

        return router
    }

    private fun createNewBurracoGameHandler(routingContext: RoutingContext) {
        try {
            val outcome: Outcome = controllerAdapter.createNewBurracoGame(GameIdentity.create())
            when (outcome) {
                is Valid -> routingContext.response()
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .setStatusCode(200)
                    .end(Json.encodePrettily(outcome))
                is Invalid -> routingContext.response()
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .setStatusCode(400)
                    .end(
                        Json.encodePrettily(
                            ErrorMsgModule(
                                code = 400,
                                errorMessages = mapOf("error" to outcome.err.toString())
                            )
                        )
                    )
            }
        } catch (e: Exception) {
            throw e
        }
    }

    private fun joinPlayerHandler(routingContext: RoutingContext) {
        val requestJson = routingContext.bodyAsJson
        try {
            val gameIdentity = GameIdentity.create(routingContext.request().getParam("gameId"))!!
            val playerIdentity = PlayerIdentity.create(requestJson.getString("playerIdentity"))!!
            val eventStoreVertxAdapter = (eventStore as EventStoreVertxAdapter)
            eventStoreVertxAdapter
                .getEvents(gameIdentity.convertTo().toString(), "BurracoGame")
                .future()
                .onComplete { ar ->
                    eventStoreVertxAdapter
                        .loadExtendedEvents(gameIdentity.convertTo().toString(), ar.result())
                    when (val outcome = controllerAdapter.joinPlayer(gameIdentity, playerIdentity = playerIdentity)) {
                        is Valid -> routingContext.response()
                            .putHeader("content-type", "application/json; charset=utf-8")
                            .setStatusCode(200)
                            .end(Json.encodePrettily(outcome))
                        is Invalid -> routingContext.response()
                            .putHeader("content-type", "application/json; charset=utf-8")
                            .setStatusCode(400)
                            .end(Json.encodePrettily(ErrorMsgModule(code = 400, errorMessages = mapOf("error" to outcome.err.toString()))))
                    }
                }.onFailure {
                    throw it
                }
        } catch (e: Exception) {
            throw e
        }
    }



    private fun failureHandler(routingContext: RoutingContext) {
        val errorMsgModule = when (val failure = routingContext.failure()) {
            is ValidationException -> ErrorMsgModule(
                code = 400, errorMessages = mapOf(
                    ("message" to failure.message),
                    ("cause" to failure.cause),
                    ("stacktrace" to failure.stackTrace.map { st -> "${st.fileName} ${st.methodName} (${st.lineNumber})" })
                )
            )
            else -> ErrorMsgModule(
                code = 500, errorMessages = mapOf(
                    ("message" to failure.message),
                    ("cause" to failure.cause),
                    ("stacktrace" to failure.stackTrace.map { st -> "${st.fileName} ${st.methodName} (${st.lineNumber})" })
                )
            )
        }

        routingContext.response()
            .putHeader("content-type", "application/json; charset=utf-8")
            .setStatusCode(errorMsgModule.code)
            .end(Json.encodePrettily(errorMsgModule))
    }

}