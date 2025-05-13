package com.abaddon83.burraco.game.adapters.commandController.rest.handlers

import com.abaddon83.burraco.common.helpers.Validated
import com.abaddon83.burraco.game.ports.CommandControllerPort
import io.vertx.ext.web.RoutingContext

class NewGameRoutingHandler(private val controllerAdapter: CommandControllerPort) : CoroutineRoutingHandler() {

    override suspend fun coroutineHandle(routingContext: RoutingContext) {
        when (val outcome = controllerAdapter.createGame()) {
                is Validated.Valid -> commandExecuted(routingContext, outcome.value)
                is Validated.Invalid -> commandNotExecuted(routingContext, outcome.err)
            }
    }


}