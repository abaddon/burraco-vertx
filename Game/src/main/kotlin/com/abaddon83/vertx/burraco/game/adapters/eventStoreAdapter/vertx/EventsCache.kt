package com.abaddon83.vertx.burraco.game.adapters.eventStoreAdapter.vertx

import com.abaddon83.vertx.burraco.game.adapters.eventStoreAdapter.vertx.model.ExtendEvent
import io.vertx.core.logging.LoggerFactory

object EventsCache {
    private val log = LoggerFactory.getLogger(this::class.qualifiedName)!!
    private var extendedEventsLoaded: MutableMap<String, List<ExtendEvent>> = mutableMapOf()


    fun persist(pk: String, events: List<ExtendEvent>) {
        extendedEventsLoaded[pk] = events
        log.info("events loaded in the local cache: key: $pk, size: ${events.size}")
        log.info("list:")
        extendedEventsLoaded.get(pk)?.forEach { ev ->
            log.info(ev.name)
        }

    }

    fun getEventCached(pk: String): List<ExtendEvent> {
        log.info("${extendedEventsLoaded.keys.size}keys available:");
        extendedEventsLoaded.forEach { r ->
            log.info("key: ${r.key}");
        }
        return extendedEventsLoaded.getOrDefault(pk, listOf())
    }
}