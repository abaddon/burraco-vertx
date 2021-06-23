package com.abaddon83.burraco.game.adapters.commandController.config

import io.vertx.core.http.HttpServerOptions

data class RestApiConfig(
    val serviceName: String,
    val http: RestApiHttpConfig
){
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
