package com.abaddon83.burraco.player.adapter.commandController.rest.handlers

import com.abaddon83.burraco.player.DomainError
import com.abaddon83.burraco.player.DomainResult
import com.abaddon83.burraco.player.adapter.commandController.rest.models.ErrorMsgModule
import com.abaddon83.burraco.player.adapter.commandController.rest.models.PlayerModule
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
            .end(PlayerModule.from(domainResult.player).toJson())
    }

    protected fun commandNotExecuted(routingContext: RoutingContext, domainError: DomainError) {
        routingContext.response()
            .putHeader("content-type", "application/json; charset=utf-8")
            .setStatusCode(400)
            .end(Json.encodePrettily(ErrorMsgModule(code = 400, errorMessages = listOf(domainError.toMap()))))
    }
}