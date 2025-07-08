package com.abaddon83.burraco.player

import com.abaddon83.burraco.player.adapter.commandController.CommandControllerAdapter
import com.abaddon83.burraco.player.adapter.commandController.rest.RestHttpServiceVerticle
import com.abaddon83.burraco.player.adapter.externalEventPublisher.kafka.KafkaExternalEventPublisherAdapter
import com.abaddon83.burraco.player.adapter.projection.GameViewProjectionVerticle
import com.abaddon83.burraco.player.command.AggregatePlayerCommandHandler
import com.abaddon83.burraco.player.model.player.Player
import com.abaddon83.burraco.player.model.player.PlayerDraft
import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log
import io.github.abaddon.kcqrs.eventstoredb.eventstore.EventStoreDBRepository
import io.vertx.core.AbstractVerticle
import io.vertx.core.AsyncResult
import io.vertx.core.DeploymentOptions
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.Verticle
import io.vertx.core.Vertx


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
        log.info("Player service starting...")
        try {
            val serverOpts = DeploymentOptions().setConfig(config())
            val commandControllerAdapter = buildCommandControllerAdapter()

            val allFutures: List<Future<Any>> = listOf(
                deploy(
                    RestHttpServiceVerticle(serviceConfig, commandControllerAdapter),
                    serverOpts
                ).future(),
                deploy(
                    GameViewProjectionVerticle(
                        serviceConfig.gameProjection.eventStoreSubscriptionConfig()
                    ),
                    serverOpts
                ).future()
            )

            Future.all(allFutures).onComplete {
                if (it.succeeded()) {
                    log.info("Player MainVerticle started")
                    start()
                    log.info("IDS: ${vertx.deploymentIDs()}")
                    startPromise?.complete()
                } else {
                    log.error(it.cause().message, it.cause())
                    startPromise?.fail(it.cause())
                }
            }
        } catch (ex: Exception) {
            log.error("Player service start failed", ex)
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

    private fun buildPlayerEventStoreRepository(): EventStoreDBRepository<Player> {
        return EventStoreDBRepository(
            serviceConfig.eventStore.eventStoreDBRepositoryConfig()
        ) { PlayerDraft.empty() }
    }

    private fun buildCommandControllerAdapter(): CommandControllerAdapter {
        // External Event Publisher
        val externalEventPublisher =
            KafkaExternalEventPublisherAdapter(vertx, serviceConfig.kafkaPlayerProducer)

        // Repository
        val repository = buildPlayerEventStoreRepository()

        val aggregatePlayerCommandHandler = AggregatePlayerCommandHandler(
            repository,
            externalEventPublisher
        )

        return CommandControllerAdapter(aggregatePlayerCommandHandler)
    }
}