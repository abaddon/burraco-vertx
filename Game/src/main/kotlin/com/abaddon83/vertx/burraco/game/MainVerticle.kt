package com.abaddon83.vertx.burraco.game

import com.abaddon83.vertx.burraco.game.adapters.commandController.CommandControllerAdapter
import com.abaddon83.vertx.burraco.game.adapters.commandController.RestApiVerticle
import com.abaddon83.vertx.burraco.game.adapters.commandController.config.HttpConfig
import com.abaddon83.vertx.burraco.game.adapters.dealerAdapter.KafkaConsumerDealerAdapter
import com.abaddon83.vertx.burraco.game.adapters.dealerAdapter.config.KafkaConsumerConfig
import com.abaddon83.vertx.burraco.game.adapters.eventBrokerProducerAdapter.KafkaGameEventsBrokerProducerAdapter
import com.abaddon83.vertx.burraco.game.adapters.eventBrokerProducerAdapter.config.KafkaProducerConfig
import com.abaddon83.vertx.burraco.game.adapters.eventStoreAdapter.tcp.EventStoreTcpBusAdapter
import com.abaddon83.vertx.burraco.game.ports.EventStorePort
import io.vertx.core.*
import org.slf4j.LoggerFactory

class MainVerticle : AbstractVerticle() {
    companion object {
        @JvmStatic
        fun main(args:Array<String>) {
            Launcher.executeCommand("run", MainVerticle::class.java.name)
        }
    }

    private val log = LoggerFactory.getLogger(this::class.qualifiedName)
    private val HTTP_HOST = "localhost"
    private val HTTP_PORT = "8080"
    private val HTTP_ROOT = "/"
    private val SERVICE_NAME = "command-api-service"

    override fun start(startPromise: Promise<Void>?) {
        val httpConfig =  HttpConfig(SERVICE_NAME,HTTP_HOST,HTTP_PORT.toInt(),HTTP_ROOT)
        val kafkaProducerConfigConfig = KafkaProducerConfig()
        val eventStore: EventStorePort = EventStoreTcpBusAdapter(vertx)
        val eventBrokerProducer = KafkaGameEventsBrokerProducerAdapter(vertx,kafkaProducerConfigConfig)

        val serverOpts = DeploymentOptions().setConfig(config())

        //list of verticle to deploy
        val allFutures: List<Future<Any>> = listOf(
            deploy(RestApiVerticle(httpConfig, CommandControllerAdapter(eventStore,eventBrokerProducer)), serverOpts).future(),
            deploy(KafkaConsumerDealerAdapter(KafkaConsumerConfig(),eventStore,eventBrokerProducer),serverOpts).future()
        )

        CompositeFuture.all(allFutures).onComplete{
            if (it.succeeded()) {
                log.info("MainVerticle started")
                start()
                log.info("IDS: ${vertx.deploymentIDs()}")
                startPromise?.complete()
            } else {
                startPromise?.fail(it.cause())
            }
        }
    }

    override fun stop(stopPromise: Promise<Void?>) {
        val ids = vertx.deploymentIDs()
        log.info("Undeployed ok: {}",ids)
        for (id in ids) {
            vertx.undeploy(id) { res: AsyncResult<Void?> ->
                if (res.succeeded()) {
                    log.info("Undeployed ok")
                    stopPromise.complete()
                } else {
                    log.error("Undeploy failed!",res.cause())
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
