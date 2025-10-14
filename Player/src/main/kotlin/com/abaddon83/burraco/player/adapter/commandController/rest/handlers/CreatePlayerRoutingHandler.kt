package com.abaddon83.burraco.player.adapter.commandController.rest.handlers

import com.abaddon83.burraco.common.helpers.Validated
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.player.adapter.commandController.rest.models.ErrorMsgModule
import com.abaddon83.burraco.player.port.CommandControllerPort
import com.abaddon83.burraco.player.projection.GameState
import com.abaddon83.burraco.player.projection.gameview.InMemoryGameViewRepository
import io.github.abaddon.kcqrs.core.helpers.KcqrsLoggerFactory.log
import io.vertx.core.Future
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.validation.RequestParameters
import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.kotlin.core.json.get

class CreatePlayerRoutingHandler(
    private val controllerAdapter: CommandControllerPort,
    private val gameViewRepository: InMemoryGameViewRepository
) : CoroutineRoutingHandler() {

    override suspend fun coroutineHandle(routingContext: RoutingContext) {
        val params: RequestParameters = routingContext.get(ValidationHandler.REQUEST_CONTEXT_KEY)
        val bodyRequest: JsonObject = params.body().jsonObject
        log.debug("CreatePlayerRoutingHandler request body: ${bodyRequest.encode()}")
        val gameIdentity = GameIdentity(bodyRequest.get<String>("gameId"))
        val user = bodyRequest.get<String>("user")

        // ============ VALIDATION LOGIC ============
        log.debug("Validating game ${gameIdentity.valueAsString()} before creating player")
        val gameView = gameViewRepository.findByGameId(gameIdentity)

        when {
            gameView == null -> responseWithError(
                routingContext,
                404,
                "GameNotFound",
                "Game ${gameIdentity.valueAsString()} not found"
            )

            gameView.isFull() -> responseWithError(
                routingContext,
                409,
                "GameFull",
                "Game is full (${gameView.players.size}/${gameView.maxPlayers} players)"
            )

            gameView.state != GameState.DRAFT -> responseWithError(
                routingContext,
                409,
                "InvalidGameState",
                "Game is in ${gameView.state} state, cannot add players"
            )

            else -> createPlayer(routingContext, gameIdentity, user)
        }
    }

    private suspend fun createPlayer(routingContext: RoutingContext, gameIdentity: GameIdentity, user: String) {
        log.info("Game ${gameIdentity.valueAsString()} validation passed, proceeding with player creation")
        when (val outcome = controllerAdapter.createPlayer(gameIdentity, user)) {
            is Validated.Valid -> commandExecuted(routingContext, outcome.value)
            is Validated.Invalid -> commandNotExecuted(routingContext, outcome.err)
        }
    }

    private fun responseWithError(
        routingContext: RoutingContext,
        statusCode: Int,
        error: String,
        message: String
    ): Future<Void> {
        log.warn(message)
        return routingContext
            .response()
            .setStatusCode(statusCode)
            .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .end(
                Json.encodePrettily(
                    ErrorMsgModule(
                        code = statusCode,
                        errorMessages = listOf(
                            mapOf(
                                "error" to error,
                                "message" to message
                            )
                        )
                    )
                )
            )
    }
}