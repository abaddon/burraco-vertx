package com.abaddon83.burraco.player.adapter.commandController.rest.handlers

import com.abaddon83.burraco.common.helpers.Validated
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.player.port.CommandControllerPort
import io.github.abaddon.kcqrs.core.helpers.KcqrsLoggerFactory.log
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.validation.RequestParameters
import io.vertx.ext.web.validation.ValidationHandler

class DeletePlayerRoutingHandler(private val controllerAdapter: CommandControllerPort) : CoroutineRoutingHandler() {

    override suspend fun coroutineHandle(routingContext: RoutingContext) {
        val params: RequestParameters = routingContext.get(ValidationHandler.REQUEST_CONTEXT_KEY)
        log.debug("DeletePlayerRoutingHandler request with playerId: ${params.pathParameter("playerId").string}")
        
        val playerIdentity = PlayerIdentity(params.pathParameter("playerId").string)
        
        when (val outcome = controllerAdapter.deletePlayer(playerIdentity)) {
            is Validated.Valid -> commandExecuted(routingContext, outcome.value)
            is Validated.Invalid -> commandNotExecuted(routingContext, outcome.err)
        }
    }
}