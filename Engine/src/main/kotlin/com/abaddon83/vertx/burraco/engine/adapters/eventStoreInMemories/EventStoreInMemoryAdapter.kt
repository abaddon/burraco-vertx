package com.abaddon83.vertx.burraco.engine.adapters.eventStoreInMemories

import com.abaddon83.utils.es.Event
import com.abaddon83.vertx.burraco.engine.events.BurracoGameEvent
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


