package com.abaddon83.vertx.burraco.game.ports

import com.abaddon83.burraco.common.events.BurracoGameEvent
import com.abaddon83.utils.ddd.Event
import io.vertx.core.Promise


abstract class EventStorePort {

    abstract fun save(events: Iterable<Event>)

    inline fun <reified T: Event> getEvents(pk: String): List<T> =
            when (T::class) {
                BurracoGameEvent::class ->  getBurracoGameEvents(pk) as List<T>
                else -> emptyList()
            }
//    inline fun <reified T: Event> getEvents(pk: String): Promise<List<T>> =
//        when (T::class) {
//            BurracoGameEvent::class ->  getBurracoGameEvents(pk) as Promise<List<T>>
//            else -> Promise.promise()
//        }

    abstract fun getBurracoGameEvents(pk: String): List<BurracoGameEvent>
}