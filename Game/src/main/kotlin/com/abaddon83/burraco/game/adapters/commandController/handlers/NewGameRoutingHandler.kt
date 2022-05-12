package com.abaddon83.burraco.game.adapters.commandController.handlers

import com.abaddon83.burraco.game.ExceptionError
import com.abaddon83.burraco.game.helpers.Validated
import com.abaddon83.burraco.game.models.game.GameIdentity
import com.abaddon83.burraco.game.ports.CommandControllerPort
import io.vertx.ext.web.RoutingContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class NewGameRoutingHandler(private val controllerAdapter: CommandControllerPort) : CoroutineRoutingHandler() {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun coroutineHandle(routingContext: RoutingContext) {
//        val params: RequestParameters = routingContext.get(ValidationHandler.REQUEST_CONTEXT_KEY)
//        val bodyRequest = params.body()
        try {
            when (val outcome = controllerAdapter.createGame()) {
                is Validated.Valid -> commandExecuted(routingContext, outcome.value)
                is Validated.Invalid -> commandNotExecuted(routingContext, outcome.err)
            }
        } catch (e: Exception) {
            log.error(e.message,e)
            commandNotExecuted(routingContext, ExceptionError(e))
        }
    }


}