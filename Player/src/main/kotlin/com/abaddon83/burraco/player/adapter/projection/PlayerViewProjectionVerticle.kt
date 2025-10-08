package com.abaddon83.burraco.player.adapter.projection

import com.abaddon83.burraco.player.projection.playerview.PlayerView
import com.abaddon83.burraco.player.projection.playerview.PlayerViewProjectionHandler
import com.abaddon83.burraco.player.projection.playerview.PlayerViewRepository
import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log
import io.github.abaddon.kcqrs.eventstoredb.eventstore.EventStoreDomainEventSubscriber
import io.github.abaddon.kcqrs.eventstoredb.eventstore.EventStoreSubscriptionConfig
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise

class PlayerViewProjectionVerticle(
    private val playerEventStoreSubscriptionConfig: EventStoreSubscriptionConfig
) : AbstractVerticle() {

    private lateinit var projectionHandler: PlayerViewProjectionHandler
    lateinit var repository: PlayerViewRepository // Make it public for query access

    override fun start(startPromise: Promise<Void>) {
        log.info("Starting PlayerView Projection Verticle...")

        try {
            // Initialize repository
            repository = PlayerViewRepository()

            // Initialize projection handler
            val playerViewProjectionHandler = PlayerViewProjectionHandler(repository = repository)
            projectionHandler = playerViewProjectionHandler

            val eventStoreDomainEventSubscriber =
                EventStoreDomainEventSubscriber<PlayerView>(playerEventStoreSubscriptionConfig)

            eventStoreDomainEventSubscriber.subscribe(playerViewProjectionHandler)

            log.info("PlayerView Projection Verticle started successfully")
            startPromise.complete()

        } catch (ex: Exception) {
            log.error("Failed to start PlayerView Projection Verticle", ex)
            startPromise.fail(ex)
        }
    }

    override fun stop(stopPromise: Promise<Void>) {
        log.info("Stopping PlayerView Projection Verticle...")

        try {
            projectionHandler.stop(null)

            log.info("PlayerView Projection Verticle stopped successfully")
            stopPromise.complete()

        } catch (ex: Exception) {
            log.error("Error stopping PlayerView Projection Verticle", ex)
            stopPromise.fail(ex)
        }
    }
}
