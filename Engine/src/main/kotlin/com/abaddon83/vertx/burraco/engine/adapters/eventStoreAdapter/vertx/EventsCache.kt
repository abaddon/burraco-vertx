package com.abaddon83.vertx.burraco.engine.adapters.eventStoreAdapter.vertx

import com.abaddon83.vertx.burraco.engine.adapters.eventStoreAdapter.vertx.model.ExtendEvent
import io.vertx.core.logging.LoggerFactory

object EventsCache {
    private val log = LoggerFactory.getLogger(this::class.qualifiedName)!!
    private var extendedEventsLoaded : MutableMap<String,Set<ExtendEvent>> = mutableMapOf()


    fun persist(pk: String,events: Set<ExtendEvent>){
        extendedEventsLoaded[pk] = events
    }

    fun getEventCached(pk: String):Set<ExtendEvent>{
        log.info("${extendedEventsLoaded.keys.size}keys available:");
        extendedEventsLoaded.forEach {  r ->
            log.info("key: ${r.key}");
        }
        return extendedEventsLoaded.getOrDefault(pk, setOf())
    }
}