package com.abaddon83.burraco.game.adapters.commandController.handlers

import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

interface CoroutineRoutingHandler: Handler<RoutingContext> {

    suspend fun coroutineHandle(routingContext: RoutingContext)

    override fun handle(routingContext: RoutingContext) {
        GlobalScope.launch(routingContext.vertx().dispatcher()) {
            coroutineHandle(routingContext)
        }
    }
}