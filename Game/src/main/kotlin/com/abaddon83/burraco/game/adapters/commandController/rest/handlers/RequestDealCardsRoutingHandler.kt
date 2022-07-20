package com.abaddon83.burraco.game.adapters.commandController.rest.handlers

import com.abaddon83.burraco.game.helpers.Validated
import com.abaddon83.burraco.game.models.game.GameIdentity
import com.abaddon83.burraco.game.models.player.PlayerIdentity
import com.abaddon83.burraco.game.ports.CommandControllerPort
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.validation.RequestParameters
import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.kotlin.core.json.get
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RequestDealCardsRoutingHandler(private val controllerAdapter: CommandControllerPort) : CoroutineRoutingHandler() {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun coroutineHandle(routingContext: RoutingContext) {
        val params: RequestParameters = routingContext.get(ValidationHandler.REQUEST_CONTEXT_KEY)
        val bodyRequest: JsonObject = params.body().jsonObject
        log.debug("RequestDealCardsRoutingHandler request body: ${bodyRequest.encode()}")
        val gameIdentity = GameIdentity.create(params.pathParameter("gameId").string)
        val playerIdentity = PlayerIdentity.create(bodyRequest.get<String>("playerId"))
        when (val outcome = controllerAdapter.requestDealCards(gameIdentity, playerIdentity)) {
            is Validated.Valid -> commandExecuted(routingContext, outcome.value)
            is Validated.Invalid -> commandNotExecuted(routingContext, outcome.err)
        }
    }
}