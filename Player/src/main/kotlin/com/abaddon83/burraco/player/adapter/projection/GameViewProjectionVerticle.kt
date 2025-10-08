package com.abaddon83.burraco.player.adapter.projection

import com.abaddon83.burraco.player.projection.gameview.GameView
import com.abaddon83.burraco.player.projection.gameview.GameViewProjectionHandler
import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log
import io.github.abaddon.kcqrs.core.persistence.IProjectionRepository
import io.github.abaddon.kcqrs.eventstoredb.eventstore.EventStoreDomainEventSubscriber
import io.github.abaddon.kcqrs.eventstoredb.eventstore.EventStoreSubscriptionConfig
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise

class GameViewProjectionVerticle(
    private val gameEventStoreSubscriptionConfig: EventStoreSubscriptionConfig,
    private val repository: IProjectionRepository<GameView>
) : AbstractVerticle() {

    private val gameViewProjectionHandler: GameViewProjectionHandler = GameViewProjectionHandler(repository)


    override fun start(startPromise: Promise<Void>) {
        log.info("Starting GameView Projection Verticle...")

        try {
            val eventStoreDomainEventSubscriber =
                EventStoreDomainEventSubscriber<GameView>(gameEventStoreSubscriptionConfig)

            eventStoreDomainEventSubscriber.subscribe(gameViewProjectionHandler)


            log.info("GameView Projection Verticle started successfully")
            startPromise.complete()

        } catch (ex: Exception) {
            log.error("Failed to start GameView Projection Verticle", ex)
            startPromise.fail(ex)
        }
    }

    override fun stop(stopPromise: Promise<Void>) {
        log.info("Stopping GameView Projection Verticle...")

        try {
            gameViewProjectionHandler.stop(null)

            log.info("GameView Projection Verticle stopped successfully")
            stopPromise.complete()

        } catch (ex: Exception) {
            log.error("Error stopping GameView Projection Verticle", ex)
            stopPromise.fail(ex)
        }
    }
}