package com.abaddon83.burraco.player.adapter.commandController.rest.handlers

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.player.adapter.commandController.rest.models.ErrorMsgModule
import com.abaddon83.burraco.player.adapter.commandController.rest.models.PlayerViewModule
import com.abaddon83.burraco.player.port.QueryControllerPort
import io.github.abaddon.kcqrs.core.helpers.KcqrsLoggerFactory.log
import io.vertx.core.Handler
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.Json
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.validation.RequestParameters
import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class GetPlayerViewRoutingHandler(private val queryController: QueryControllerPort) : Handler<RoutingContext> {

    override fun handle(routingContext: RoutingContext) {
        CoroutineScope(Job() + routingContext.vertx().dispatcher()).launch {
            coroutineHandle(routingContext)
        }
    }

    private suspend fun coroutineHandle(routingContext: RoutingContext) {
        val params: RequestParameters = routingContext.get(ValidationHandler.REQUEST_CONTEXT_KEY)
        val playerId = params.pathParameter("playerId").string
        val gameId = params.pathParameter("gameId").string

        log.debug("GetPlayerViewRoutingHandler - playerId: $playerId, gameId: $gameId")

        val playerIdentity = PlayerIdentity(playerId)
        val gameIdentity = GameIdentity(gameId)

        val result = queryController.getPlayerView(playerIdentity, gameIdentity)

        if (result.isSuccess) {
            routingContext
                .response()
                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .setStatusCode(200)
                .end(PlayerViewModule.from(result.getOrThrow()).toJson())
        } else {
            log.error("Failed to get player view", result.exceptionOrNull())
            routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .setStatusCode(404)
                .end(Json.encodePrettily(ErrorMsgModule(code = 404, errorMessages = listOf(mapOf("error" to (result.exceptionOrNull()?.message ?: "Player view not found"))))))
        }
    }
}
