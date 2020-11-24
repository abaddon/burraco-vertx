package com.abaddon83.vertx.burraco.engine.adapters.eventStoreAdapter.inMemory

import com.abaddon83.utils.ddd.Event
import com.abaddon83.burraco.common.events.BurracoGameEvent
import com.abaddon83.vertx.burraco.engine.ports.EventStorePort

//Test Purpose
class EventStoreInMemoryAdapter : EventStorePort() {

    private  val eventStore: EventStoreInMemory =  EventStoreInMemory
    override fun save(events: Iterable<Event>) {
        eventStore.save(events)
    }

    override fun getBurracoGameEvents(pk: String): List<BurracoGameEvent> {
        return eventStore.getEvents(pk).map { event ->
            when(event){
                is BurracoGameEvent -> event
                else -> TODO()
            }
        }
    }
}


