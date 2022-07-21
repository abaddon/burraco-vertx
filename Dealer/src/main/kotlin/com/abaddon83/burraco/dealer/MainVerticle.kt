package com.abaddon83.burraco.dealer

import com.abaddon83.burraco.dealer.adapters.commandController.CommandControllerAdapter
import com.abaddon83.burraco.dealer.adapters.commandController.kafka.KafkaGameConsumerAdapter
import io.vertx.core.*
import org.slf4j.LoggerFactory

class MainVerticle(
    private val configPath: String
) : AbstractVerticle() {
    private val log = LoggerFactory.getLogger(this::class.qualifiedName)

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val vertx=Vertx.vertx()
            val configPath= when(args[0]){
                is String -> args[0]
                else -> "Dealer/local_config.yml"
            }
            vertx.deployVerticle(MainVerticle(configPath))
        }
    }

    override fun start(startPromise: Promise<Void>?) {
        val serviceConfig = ServiceConfig.load(configPath)



        val commandController= CommandControllerAdapter()

        val serverOpts = DeploymentOptions().setConfig(config())

        //list of verticle to deploy
        val allFutures: List<Future<Any>> = listOf(
            deploy(
                KafkaGameConsumerAdapter(serviceConfig.gameEventConsumer, commandController),
                serverOpts
            ).future()
        )

        CompositeFuture.all(allFutures).onComplete {
            if (it.succeeded()) {
                log.info("MainVerticle started")
                start()
                log.info("IDS: ${vertx.deploymentIDs()}")
                startPromise?.complete()
            } else {
                log.error(it.cause().message, it.cause())
                startPromise?.fail(it.cause())
            }
        }
    }

    override fun stop(stopPromise: Promise<Void?>) {
        val ids = vertx.deploymentIDs()
        log.info("Undeployed ok: {}", ids)
        for (id in ids) {
            vertx.undeploy(id) { res: AsyncResult<Void?> ->
                if (res.succeeded()) {
                    log.info("Undeployed ok")
                    stopPromise.complete()
                } else {
                    log.error("Undeploy failed!", res.cause())
                    stopPromise.fail(res.cause())
                }
            }
        }
    }

    private fun deploy(verticle: Verticle, opts: DeploymentOptions): Promise<Any> {
        val done = Promise.promise<Any>()

        vertx.deployVerticle(verticle, opts) {
            if (it.failed()) {
                log.error("Failed to deploy verticle ${verticle::class.qualifiedName}")
                done.fail(it.cause())
            } else {
                log.info("Verticle deployed ${verticle::class.qualifiedName}")
                done.complete()
            }
        }
        return done
    }
}
