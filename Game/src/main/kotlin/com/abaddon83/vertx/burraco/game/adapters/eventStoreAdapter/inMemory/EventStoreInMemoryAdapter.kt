package com.abaddon83.vertx.burraco.game.adapters.eventStoreAdapter.inMemory

import com.abaddon83.utils.ddd.Event
import com.abaddon83.burraco.common.events.BurracoGameEvent
import com.abaddon83.vertx.burraco.game.ports.EventStorePort
import io.vertx.core.Promise
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.runBlocking

//Test Purpose
class EventStoreInMemoryAdapter : EventStorePort() {

    private  val eventStore: EventStoreInMemory =  EventStoreInMemory
    override fun save(events: Iterable<Event>) {
        eventStore.save(events)
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


