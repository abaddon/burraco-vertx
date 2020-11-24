package com.abaddon83.vertx.burraco.engine.adapters.commandController

import com.abaddon83.utils.functionals.Invalid
import com.abaddon83.utils.functionals.Valid
import com.abaddon83.vertx.burraco.engine.adapters.commandController.bodyRequests.CreateGameRequest
import com.abaddon83.vertx.burraco.engine.adapters.commandController.bodyRequests.JoinGameRequest
import com.abaddon83.vertx.burraco.engine.adapters.commandController.models.ErrorMsgModule
import com.abaddon83.vertx.burraco.engine.adapters.eventStoreAdapter.vertx.EventStoreVertxAdapter
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.vertx.burraco.engine.adapters.commandController.bodyRequests.StartGameRequest
import com.abaddon83.vertx.burraco.engine.adapters.eventStoreAdapter.vertx.model.ExtendEvent
import com.abaddon83.vertx.burraco.engine.ports.CommandControllerPort
import com.abaddon83.vertx.burraco.engine.ports.EventStorePort
import com.abaddon83.vertx.burraco.engine.ports.Outcome
import io.vertx.core.AsyncResult
import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.api.validation.ValidationException
import io.vertx.ext.web.handler.BodyHandler


class CommandControllerRoutes(private val vertx: Vertx) {

    private val eventStore: EventStorePort
        get() = EventStoreVertxAdapter(vertx)

    private val controllerAdapter: CommandControllerPort
        get() = CommandControllerAdapter(vertx)

    fun getRouters(): Router {
        val router: Router = Router.router(vertx)
        router.route().handler(BodyHandler.create())

        router.post("/games/burraco").handler(CreateGameRequest.getValidator()).handler(::createNewBurracoGameHandler)
            .failureHandler(::failureHandler)
        router.post("/games/burraco/:gameId/join").handler(JoinGameRequest.getValidator()).handler(::joinPlayerHandler)
            .failureHandler(::failureHandler)
        router.post("/games/burraco/:gameId/start").handler(StartGameRequest.getValidator())
            .handler(::startPlayerHandler)
            .failureHandler(::failureHandler)

        return router
    }

    private fun createNewBurracoGameHandler(routingContext: RoutingContext) {
        try {
            when (val outcome: Outcome = controllerAdapter.createNewBurracoGame(GameIdentity.create())) {
                is Valid -> routingContext.response()
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .setStatusCode(200)
                    .end(Json.encodePrettily(outcome))
                is Invalid -> routingContext.response()
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .setStatusCode(400)
                    .end(Json.encodePrettily(ErrorMsgModule(code = 400, errorMessages = listOf(outcome.err.toMap()))))
            }
        } catch (e: Exception) {
            throw e
        }
    }


    private fun joinPlayerHandler(routingContext: RoutingContext) {
        val requestJson = routingContext.bodyAsJson
        val gameIdentity = GameIdentity.create(routingContext.request().getParam("gameId"))!!
        val playerIdentity = PlayerIdentity.create(requestJson.getString("playerIdentity"))!!
        updateLocalEventsCache(gameIdentity) {
            when (val outcome = controllerAdapter.joinPlayer(gameIdentity, playerIdentity = playerIdentity)) {
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
                                errorMessages = listOf(outcome.err.toMap())
                            )
                        )
                    )
            }
        }
    }

    private fun startPlayerHandler(routingContext: RoutingContext) {
        val requestJson = routingContext.bodyAsJson
        val gameIdentity = GameIdentity.create(routingContext.request().getParam("gameId"))!!
        val playerIdentity = PlayerIdentity.create(requestJson.getString("playerIdentity"))!!
        updateLocalEventsCache(gameIdentity) {
            when (val outcome = controllerAdapter.startGame(gameIdentity, playerIdentity = playerIdentity)) {
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
                                errorMessages = listOf(outcome.err.toMap())
                            )
                        )
                    )
            }
        }
    }


    private fun updateLocalEventsCache(
        aggregateIdentity: GameIdentity,
        block: (AsyncResult<List<ExtendEvent>>) -> Unit
    ) {
        val eventStoreVertxAdapter = (eventStore as EventStoreVertxAdapter)
        eventStoreVertxAdapter
            //retrieve the events from the EventStore
            .getEvents(aggregateIdentity.convertTo().toString(), "BurracoGame")
            .future()
            .onComplete { ar ->
                //load events in a local cache.. workaround to adapt the current model to Vertx..
                eventStoreVertxAdapter
                    .loadExtendedEvents(aggregateIdentity.convertTo().toString(), ar.result())
                block(ar)
            }.onFailure {
                throw it
            }
    }

    private fun failureHandler(routingContext: RoutingContext) {
        val errorMsgModule = when (val failure = routingContext.failure()) {
            is ValidationException -> ErrorMsgModule(
                code = 400, errorMessages = listOf(
                    mapOf(
                        ("message" to failure.message),
                        ("cause" to failure.cause),
                        ("stacktrace" to failure.stackTrace.map { st -> "${st.fileName} ${st.methodName} (${st.lineNumber})" })
                    )
                )
            )
            else -> ErrorMsgModule(
                code = 500, errorMessages = listOf(
                    mapOf(
                        ("message" to failure.message),
                        ("cause" to failure.cause),
                        ("stacktrace" to failure.stackTrace.map { st -> "${st.fileName} ${st.methodName} (${st.lineNumber})" })
                    )
                )
            )
        }

        routingContext.response()
            .putHeader("content-type", "application/json; charset=utf-8")
            .setStatusCode(errorMsgModule.code)
            .end(Json.encodePrettily(errorMsgModule))
    }

}