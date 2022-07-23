package com.abaddon83.burraco.game

import com.abaddon83.burraco.game.adapters.commandController.rest.RestHttpServiceVerticle
import com.abaddon83.burraco.game.adapters.externalEventPublisher.kafka.KafkaExternalEventPublisherAdapter
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameDraft
import io.github.abaddon.kcqrs.eventstoredb.eventstore.EventStoreDBRepository
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
            val configPath= when(args.size){
                1 -> args[0]
                else -> "./local_config.yml"
            }
            vertx.deployVerticle(MainVerticle(configPath))
        }
    }

    override fun start(startPromise: Promise<Void>?) {
        val serviceConfig = ServiceConfig.load(configPath)

        //GameEventsPublisher
        val externalEventPublisher = KafkaExternalEventPublisherAdapter(vertx, serviceConfig.gameEventPublisher)

        //Repository
        val repository = EventStoreDBRepository<Game>(serviceConfig.eventStoreDBRepository) { GameDraft.empty() }

        val serverOpts = DeploymentOptions().setConfig(config())

        //list of verticle to deploy
        val allFutures: List<Future<Any>> = listOf(
            deploy(
                RestHttpServiceVerticle.build(serviceConfig.restHttpService, repository, externalEventPublisher),
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
        log.info("Undeploy started..: {}", ids)
        if(ids.isEmpty()){
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
}
