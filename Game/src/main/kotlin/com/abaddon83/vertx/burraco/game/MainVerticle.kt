package com.abaddon83.vertx.burraco.game

import com.abaddon83.vertx.burraco.game.adapters.commandController.CommandControllerAdapter
import com.abaddon83.vertx.burraco.game.adapters.commandController.RestApiVerticle
import com.abaddon83.vertx.burraco.game.adapters.commandController.config.HttpConfig
import io.vertx.core.*
import io.vertx.servicediscovery.ServiceDiscoveryOptions
import io.vertx.servicediscovery.Record
import org.slf4j.LoggerFactory

class MainVerticle : AbstractVerticle() {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.qualifiedName)
    }

    private val HTTP_HOST = "localhost"
    private val HTTP_PORT = "8080"
    private val HTTP_ROOT = "/"
    private val SERVICE_NAME = "command-api-service"

    private val records: MutableList<Record> = mutableListOf()

    private val serviceDiscoveryOptions: ServiceDiscoveryOptions = ServiceDiscoveryOptions()
        .setAnnounceAddress("vertx.discovery.announce")
        .setName("my-name")

    override fun start(startPromise: Promise<Void>?) {
        //val discovery: ServiceDiscovery = ServiceDiscovery.create(vertx, serviceDiscoveryOptions)
        val serverOpts = DeploymentOptions().setConfig(config())

        val httpConfig = HttpConfig(SERVICE_NAME,HTTP_HOST,HTTP_PORT.toInt(),HTTP_ROOT)

        //list of verticle to deploy
        val allFutures: List<Future<Any>> = listOf(
            deploy(RestApiVerticle(httpConfig, CommandControllerAdapter(vertx)), serverOpts).future()
        )

        CompositeFuture.all(allFutures).onComplete{
            if (it.succeeded()) {
                log.info("MainVerticle started")
                startPromise?.complete()
            } else {
                startPromise?.fail(it.cause())
            }

        }
    }

    fun stop(stopFuture: Future<Void>?) {
        val ids = vertx.deploymentIDs()
        for (s in ids) {
            log.info(this.deploymentID())
        }
    }

    private fun deploy(name: String, opts: DeploymentOptions): Promise<Any> {
        val done = Promise.promise<Any>()

        vertx.deployVerticle(name, opts) {
            if (it.failed()) {
                log.error("Failed to deploy verticle $name")
                done.fail(it.cause())
            } else {
                log.info("Deployed verticle $name")
                done.complete()
            }
        }

        return done
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

    private fun undeploy(name: String, opts: DeploymentOptions): Promise<Any> {
        val done = Promise.promise<Any>()

        vertx.deployVerticle(name, opts) {
            if (it.failed()) {
                log.error("Failed to deploy verticle $name")
                done.fail(it.cause())
            } else {
                log.info("Deployed verticle $name")
                done.complete()
            }
        }

        return done
    }
}
