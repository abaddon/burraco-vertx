package com.abaddon83.burraco.game.ports

import com.abaddon83.burraco.common.events.BurracoGameEvent
import com.abaddon83.utils.ddd.Event
import io.vertx.core.Handler
import io.vertx.core.Promise


interface EventStorePort {
    fun save(events: Iterable<Event>, handler: Handler<Boolean>)
    fun getBurracoGameEvents(pk: String): Promise<List<BurracoGameEvent>>
}