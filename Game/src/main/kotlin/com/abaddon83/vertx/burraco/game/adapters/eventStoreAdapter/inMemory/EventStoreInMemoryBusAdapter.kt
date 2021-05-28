package com.abaddon83.vertx.burraco.game.adapters.eventStoreAdapter.inMemory

import com.abaddon83.utils.ddd.Event
import com.abaddon83.burraco.common.events.BurracoGameEvent
import com.abaddon83.vertx.burraco.game.ports.EventStorePort
import io.vertx.core.Handler
import io.vertx.core.Promise

//Test Purpose
class EventStoreInMemoryBusAdapter : EventStorePort() {

    private  val eventStore: EventStoreInMemory =  EventStoreInMemory

    override fun save(events: Iterable<Event>, handler: Handler<Boolean>) {
        eventStore.save(events)
        handler.handle(true)
    }

//    override fun getBurracoGameEvents(pk: String): List<BurracoGameEvent> {
//        return eventStore.getEvents(pk).mapNotNull { event ->
//            when (event) {
//                is BurracoGameEvent -> event as BurracoGameEvent
//                else -> null
//            }
//        }.toList()
//
//    }

    override fun getBurracoGameEvents(pk: String): Promise<List<BurracoGameEvent>> {
        val resultPromise: Promise<List<BurracoGameEvent>> = Promise.promise()
        val list: List<BurracoGameEvent>  = eventStore.getEvents(pk).mapNotNull { event ->
            when (event) {
                is BurracoGameEvent -> event as BurracoGameEvent
                else -> null
            }
        }.toList()
        resultPromise.complete(list)

        return resultPromise
    }
}


