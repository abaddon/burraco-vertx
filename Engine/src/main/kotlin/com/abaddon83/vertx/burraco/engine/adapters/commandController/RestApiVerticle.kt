package com.abaddon83.vertx.burraco.engine.adapters.commandController

import com.abaddon83.utils.vertx.AbstractHttpServiceVerticle
import io.vertx.core.logging.LoggerFactory

class RestApiVerticle: AbstractHttpServiceVerticle() {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.qualifiedName)!!
    }
    override suspend fun start() {
        val commandControllerRoutes = CommandControllerRoutes(vertx)
        val router = commandControllerRoutes.getRouters()

        startServer(8080, router)
    }
}