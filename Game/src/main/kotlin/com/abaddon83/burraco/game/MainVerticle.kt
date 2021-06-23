package com.abaddon83.burraco.game

import com.abaddon83.burraco.game.adapters.commandController.CommandControllerAdapter
import com.abaddon83.burraco.game.adapters.commandController.RestApiVerticle
import com.abaddon83.burraco.game.adapters.dealerAdapter.KafkaConsumerDealerAdapter
import com.abaddon83.burraco.game.adapters.eventBrokerProducerAdapter.KafkaGameEventsBrokerProducerAdapter
import com.abaddon83.burraco.game.adapters.eventStoreAdapter.tcp.EventStoreTcpBusAdapter
import com.abaddon83.burraco.game.ports.EventStorePort
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

    override fun start(startPromise: Promise<Void>?) {
        val serviceConfig =  ServiceConfig.load()
        val kafkaProducerConfigConfig = serviceConfig.kafkaGameProducer
        val eventStore: EventStorePort = EventStoreTcpBusAdapter(vertx, serviceConfig.eventStoreTcpBus)
        val eventBrokerProducer = KafkaGameEventsBrokerProducerAdapter(vertx,kafkaProducerConfigConfig)

        val serverOpts = DeploymentOptions().setConfig(config())

        //list of verticle to deploy
        val allFutures: List<Future<Any>> = listOf(
            deploy(RestApiVerticle(serviceConfig.restApi, CommandControllerAdapter(eventStore,eventBrokerProducer)), serverOpts).future(),
            deploy(KafkaConsumerDealerAdapter(serviceConfig.kafkaDealerConsumer,eventStore,eventBrokerProducer),serverOpts).future()
        )

        CompositeFuture.all(allFutures).onComplete{
            if (it.succeeded()) {
                log.info("MainVerticle started")
                start()
                log.info("IDS: ${vertx.deploymentIDs()}")
                startPromise?.complete()
            } else {
                log.error(it.cause().message,it.cause())
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
