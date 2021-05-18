package com.abaddon83.vertx.burraco.game

import io.vertx.core.DeploymentOptions
import io.vertx.core.Launcher
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.impl.launcher.VertxCommandLauncher
import io.vertx.core.impl.launcher.VertxLifecycleHooks
import io.vertx.core.json.JsonObject


class Starter : VertxCommandLauncher(), VertxLifecycleHooks {

    override fun beforeStartingVertx(options: VertxOptions) {
        //options.eventBusOptions.setClustered(true)//.host = "127.0.0.1"
    }

    override fun afterConfigParsed(config: JsonObject?) {}

    override fun afterStartingVertx(vertx: Vertx?) {}

    override fun beforeDeployingVerticle(deploymentOptions: DeploymentOptions?) {}

    override fun beforeStoppingVertx(vertx: Vertx?) {}

    override fun afterStoppingVertx() {}

    override fun handleDeployFailed(
        vertx: Vertx?,
        mainVerticle: String?,
        deploymentOptions: DeploymentOptions?,
        cause: Throwable?
    ) {
        // Default behaviour is to close Vert.x if the deploy failed
        vertx?.close()
    }


    companion object {
        @JvmStatic
        fun main(vararg args: String) {
            System.setProperty("vertx.logger-delegate-factory-class-name","io.vertx.core.logging.Log4jLogDelegateFactory")
            Starter().dispatch(args)
        }

        fun executeCommand(cmd: String?, vararg args: String?) {
            Launcher().execute(cmd, *args)
        }
    }

}