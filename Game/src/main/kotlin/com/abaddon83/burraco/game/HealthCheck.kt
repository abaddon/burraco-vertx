package com.abaddon83.burraco.game

import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.ext.healthchecks.HealthCheckHandler
import io.vertx.ext.healthchecks.Status


class HealthCheck(val vertx: Vertx) {
    var healthCheckHandler = HealthCheckHandler.create(vertx)

    fun build(): HealthCheckHandler {;

        // Register another procedure with a timeout (2s). If the procedure does not complete in
        // the given time, the check fails.
        healthCheckHandler.register(
            "eventstore-connection",
            2000
        ) { promise ->
            //TODO implement health check
            promise.complete(Status.OK())
            //promise.complete(Status.KO())
        }

        return healthCheckHandler;
    }
}