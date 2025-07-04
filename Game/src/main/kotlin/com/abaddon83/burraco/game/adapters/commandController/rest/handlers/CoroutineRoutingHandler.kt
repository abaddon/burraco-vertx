package com.abaddon83.burraco.game.adapters.commandController.rest.handlers

import com.abaddon83.burraco.game.DomainError
import com.abaddon83.burraco.game.DomainResult
import com.abaddon83.burraco.game.adapters.commandController.rest.models.ErrorMsgModule
import com.abaddon83.burraco.game.adapters.commandController.rest.models.GameModule
import io.vertx.core.Handler
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.Json
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

abstract class CoroutineRoutingHandler : Handler<RoutingContext> {

    abstract suspend fun coroutineHandle(routingContext: RoutingContext)

    override fun handle(routingContext: RoutingContext) {
        CoroutineScope(Job() + routingContext.vertx().dispatcher()).launch {
            coroutineHandle(routingContext)
        }
    }

    protected fun commandExecuted(routingContext: RoutingContext, domainResult: DomainResult) {
        routingContext
            .response()
            .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .setStatusCode(200)
            .end(GameModule.from(domainResult.game).toJson())
    }

    protected fun commandNotExecuted(routingContext: RoutingContext, domainError: DomainError) {
        routingContext.response()
            .putHeader("content-type", "application/json; charset=utf-8")
            .setStatusCode(400)
            .end(Json.encodePrettily(ErrorMsgModule(code = 400, errorMessages = listOf(domainError.toMap()))))
    }
}