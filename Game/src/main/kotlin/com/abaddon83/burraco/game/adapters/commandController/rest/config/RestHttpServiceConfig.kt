package com.abaddon83.burraco.game.adapters.commandController.rest.config

import io.vertx.core.http.HttpServerOptions

data class RestHttpServiceConfig(
    val serviceName: String,
    val openApiPath: String,
    val http: RestApiHttpConfig
) {
    fun getHttpServerOptions(): HttpServerOptions {
        return HttpServerOptions()
            .setPort(http.port)
            .setHost(http.address)
    }
}

data class RestApiHttpConfig(
    val port: Int,
    val address: String,
    val root: String
)
