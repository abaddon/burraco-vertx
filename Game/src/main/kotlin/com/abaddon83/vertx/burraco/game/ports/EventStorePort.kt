package com.abaddon83.vertx.burraco.game.ports

import com.abaddon83.burraco.common.events.BurracoGameEvent
import com.abaddon83.utils.ddd.Event
import io.vertx.core.Handler
import io.vertx.core.Promise


abstract class EventStorePort {

    abstract fun save(events: Iterable<Event>, handler: Handler<Boolean>)

    inline fun <reified T: Event> getEvents(pk: String): Promise<List<T>> =
        when (T::class) {
            BurracoGameEvent::class ->  getBurracoGameEvents(pk) as Promise<List<T>>
            else -> Promise.promise()
        }
//    inline fun <reified T: Event> getEvents(pk: String): Promise<List<T>> =
//        when (T::class) {
//            BurracoGameEvent::class ->  getBurracoGameEvents(pk) as Promise<List<T>>
//            else -> Promise.promise()
//        }

    abstract fun getBurracoGameEvents(pk: String): Promise<List<BurracoGameEvent>>
}