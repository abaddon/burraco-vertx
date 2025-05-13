package com.abaddon83.burraco.readModel.adapters.readModelRestAdapter.config

import io.vertx.core.http.HttpServerOptions

data class HttpConfig(
    val serviceName: String ="",
    val host: String = "localhost",
    val port: Int = 8080,
    val root: String = "/"
){

    fun getHttpServerOptions(): HttpServerOptions {
        return HttpServerOptions()
            .setPort(port)
            .setHost(host)
    }
}
