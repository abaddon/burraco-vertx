package com.abaddon83.vertx.burraco.engine.ports

import com.abaddon83.burraco.common.events.BurracoGameEvent
import com.abaddon83.utils.es.Event


abstract class EventStorePort {

    abstract fun save(events: Iterable<Event>)

    inline fun <reified T: Event> getEvents(pk: String): List<T> =
            when (T::class) {
                BurracoGameEvent::class ->  getBurracoGameEvents(pk) as List<T>
                else -> emptyList()
            }

    abstract fun getBurracoGameEvents(pk: String): List<BurracoGameEvent>
}