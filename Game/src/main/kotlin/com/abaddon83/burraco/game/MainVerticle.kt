package com.abaddon83.burraco.game

import com.abaddon83.burraco.game.adapters.commandController.rest.RestHttpServiceVerticle
import com.abaddon83.burraco.game.adapters.gameEventPublisher.kafka.KafkaGameEventPublisherAdapter
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameDraft
import io.github.abaddon.kcqrs.eventstoredb.config.EventStoreDBConfig
import io.github.abaddon.kcqrs.eventstoredb.eventstore.EventStoreDBRepository
import io.github.abaddon.kcqrs.eventstoredb.eventstore.EventStoreDBRepositoryConfig
import io.vertx.core.*
import org.slf4j.LoggerFactory

class MainVerticle : AbstractVerticle() {
    private val configPath= "src/burraco-vertx/Game/local_config.yml"
    private val log = LoggerFactory.getLogger(this::class.qualifiedName)

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Launcher.executeCommand("run", MainVerticle::class.java.name)
        }
    }

    override fun start(startPromise: Promise<Void>?) {
        val serviceConfig = ServiceConfig.load(configPath)

        //GameEventsPublisher
        val gameEventPublisher = KafkaGameEventPublisherAdapter(vertx, serviceConfig.gameEventPublisher)

        //Repository
        val repository = EventStoreDBRepository<Game>(serviceConfig.eventStoreDBRepository) { GameDraft.empty() }

        val serverOpts = DeploymentOptions().setConfig(config())

        //list of verticle to deploy
        val allFutures: List<Future<Any>> = listOf(
            deploy(
                RestHttpServiceVerticle.build(serviceConfig.restHttpService, repository, gameEventPublisher),
                serverOpts
            ).future())

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
