package com.abaddon83.burraco.player.adapter.commandController.rest.handlers

import com.abaddon83.burraco.common.helpers.Validated
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.player.port.CommandControllerPort
import io.github.abaddon.kcqrs.core.helpers.KcqrsLoggerFactory.log
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.validation.RequestParameters
import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.kotlin.core.json.get

class CreatePlayerRoutingHandler(private val controllerAdapter: CommandControllerPort) : CoroutineRoutingHandler() {

    override suspend fun coroutineHandle(routingContext: RoutingContext) {
        val params: RequestParameters = routingContext.get(ValidationHandler.REQUEST_CONTEXT_KEY)
        val bodyRequest: JsonObject = params.body().jsonObject
        log.debug("CreatePlayerRoutingHandler request body: ${bodyRequest.encode()}")
        
        val gameIdentity = GameIdentity(bodyRequest.get<String>("gameId"))
        val user = bodyRequest.get<String>("user")
        
        when (val outcome = controllerAdapter.createPlayer(gameIdentity, user)) {
            is Validated.Valid -> commandExecuted(routingContext, outcome.value)
            is Validated.Invalid -> commandNotExecuted(routingContext, outcome.err)
        }
    }
}