package com.abaddon83.burraco.player.adapter.commandController.rest

import com.abaddon83.burraco.player.RestApiHttpConfig
import kotlinx.serialization.Serializable

@Serializable
data class RestHttpServiceConfig(
    val serviceName: String,
    val openApiPath: String,
    val http: RestApiHttpConfig
) {
    fun getHttpServerOptions(): io.vertx.core.http.HttpServerOptions {
        return io.vertx.core.http.HttpServerOptions()
            .setPort(http.port)
            .setHost(http.address)
    }
}
