package com.abaddon83.burraco.player.adapter.projection

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.player.projection.gameview.GameView
import com.abaddon83.burraco.player.projection.gameview.GameViewKey
import com.abaddon83.burraco.player.projection.gameview.GameViewProjectionHandler
import com.abaddon83.burraco.player.projection.gameview.GameViewRepository
import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log
import io.github.abaddon.kcqrs.eventstoredb.eventstore.EventStoreDomainEventSubscriber
import io.github.abaddon.kcqrs.eventstoredb.eventstore.EventStoreSubscriptionConfig
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise

class GameViewProjectionVerticle(
    private val gameEventStoreSubscriptionConfig: EventStoreSubscriptionConfig
) : AbstractVerticle() {

    private lateinit var projectionHandler: GameViewProjectionHandler
    private lateinit var repository: GameViewRepository

    override fun start(startPromise: Promise<Void>) {
        log.info("Starting GameView Projection Verticle...")

        try {
            // Initialize repository
            repository = GameViewRepository()

            // Initialize projection handler
            val projectionKey = GameViewKey(GameIdentity.empty())

            val gameViewProjectionHandler = GameViewProjectionHandler(repository = repository)


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
            projectionHandler.stop(null)

            log.info("GameView Projection Verticle stopped successfully")
            stopPromise.complete()

        } catch (ex: Exception) {
            log.error("Error stopping GameView Projection Verticle", ex)
            stopPromise.fail(ex)
        }
    }
}