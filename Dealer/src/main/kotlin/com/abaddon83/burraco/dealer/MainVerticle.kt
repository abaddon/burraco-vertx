package com.abaddon83.burraco.dealer

import com.abaddon83.burraco.common.VertxCoroutineScope
import com.abaddon83.burraco.dealer.adapters.commandController.CommandControllerAdapter
import com.abaddon83.burraco.dealer.adapters.commandController.kafka.KafkaGameConsumerAdapter
import com.abaddon83.burraco.dealer.adapters.externalEventPublisher.kafka.KafkaExternalEventPublisherAdapter
import com.abaddon83.burraco.dealer.commands.AggregateDealerCommandHandler
import com.abaddon83.burraco.dealer.models.Dealer
import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log
import io.github.abaddon.kcqrs.eventstoredb.eventstore.EventStoreDBRepository
import io.vertx.core.*

class MainVerticle(
    private val serviceConfig: ServiceConfig
) : AbstractVerticle() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val vertx = Vertx.vertx()
            ServiceConfig.load(vertx, { serviceConfig ->
                vertx.deployVerticle(MainVerticle(serviceConfig))
            })
        }
    }

    override fun start(startPromise: Promise<Void>?) {
        log.info("Dealer Starting...")
        try {
            val commandControllerAdapter = buildCommandController(
                serviceConfig,
                VertxCoroutineScope(vertx)
            )
            val serverOpts = DeploymentOptions().setConfig(config())

            //list of verticle to deploy
            val allFutures: List<Future<Any>> = listOf(
                deploy(KafkaGameConsumerAdapter(serviceConfig, commandControllerAdapter), serverOpts).future()
            )

            Future.all(allFutures).onComplete {
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
        } catch (ex: Exception) {
            log.error("Service start failed", ex)
            startPromise?.fail(ex.message)
            stop(Promise.promise())
        }
    }

    override fun stop(stopPromise: Promise<Void?>) {
        val ids = vertx.deploymentIDs()
        log.info("Undeploy started..: {}", ids)
        if (ids.isEmpty()) {
            log.info("Undeploy ended")
            super.stop(stopPromise)
        }
        for (id in ids) {
            vertx.undeploy(id) { res: AsyncResult<Void?> ->
                if (res.succeeded()) {
                    log.info("Undeploy ended")
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

    private fun buildCommandController(
        serviceConfig: ServiceConfig,
        vertxCoroutineScope: VertxCoroutineScope
    ): CommandControllerAdapter {

        val coroutineContext = vertxCoroutineScope.coroutineContext()
        val externalEventPublisher =
            KafkaExternalEventPublisherAdapter(vertxCoroutineScope, serviceConfig.dealerEventPublisher)

        //Repository
        val repository = EventStoreDBRepository<Dealer>(
            serviceConfig.eventStore.eventStoreDBRepositoryConfig(),
            { Dealer.empty() }
        )

        val commandHandler =
            AggregateDealerCommandHandler(repository, externalEventPublisher)
        return CommandControllerAdapter(commandHandler)
    }
}
