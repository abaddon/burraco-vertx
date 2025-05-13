package com.abaddon83.burraco.game.adapters.commandController.rest.handlers

import com.abaddon83.burraco.common.helpers.Validated
import com.abaddon83.burraco.common.helpers.log
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.game.ports.CommandControllerPort
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.validation.RequestParameters
import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.kotlin.core.json.get

class AddPlayerRoutingHandler(private val controllerAdapter: CommandControllerPort): CoroutineRoutingHandler() {

    override suspend fun coroutineHandle(routingContext: RoutingContext) {
        val params: RequestParameters = routingContext.get(ValidationHandler.REQUEST_CONTEXT_KEY)
        val bodyRequest: JsonObject = params.body().jsonObject
        log.debug("AddPlayerRoutingHandler request body: ${bodyRequest.encode()}")
        val gameIdentity = GameIdentity(params.pathParameter("gameId").string)
        val playerIdentity = PlayerIdentity(bodyRequest.get<String>("playerId"))
        when(val outcome = controllerAdapter.addPlayer(gameIdentity,playerIdentity)){
            is Validated.Valid -> commandExecuted(routingContext, outcome.value)
            is Validated.Invalid -> commandNotExecuted(routingContext, outcome.err)
        }
    }




}