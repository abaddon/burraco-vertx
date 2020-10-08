package com.abaddon83.vertx.burraco.engine.adapters.eventStoreVertx

import com.abaddon83.utils.es.Event
import com.abaddon83.vertx.burraco.engine.adapters.eventStoreVertx.model.ExtendEvent
import com.abaddon83.vertx.burraco.engine.events.BurracoGameEvent
import com.abaddon83.vertx.burraco.engine.ports.EventStorePort
import io.vertx.core.Vertx
import io.vertx.core.logging.LoggerFactory

class EventStoreVertxAdapter(vertx: Vertx) : EventStorePort() {
    private val vertx: Vertx = vertx

    companion object {
        const val SERVICE_ADDRESS = "eventstore-bus-service-address"
        private val log = LoggerFactory.getLogger(this::class.qualifiedName)!!
    }

//    private inline fun <reified T> getService(clazz: Class<T>): T {
//        val builder = ServiceProxyBuilder(vertx).setAddress(SERVICE_ADDRESS)
//        return builder.build(T::class.java)
//    }

    override fun save(events: Iterable<Event>) {
        events.forEach { event ->
            EventStoreServiceVertxEBProxy(vertx, SERVICE_ADDRESS)
                .persist(ExtendEvent(event)) { ar ->
                    if (ar.succeeded()) {
                        System.out.println("event stored: ${ar.result()}")
                    } else {
                        System.out.println("event NOT stored: ${ar.cause()}")
                    }
                }
        }
    }

    override fun getBurracoGameEvents(pk: String): List<BurracoGameEvent> {

        return listOf<BurracoGameEvent>()
//        return eventStore.getEvents(pk).map { event ->
//            when (event) {
//                is BurracoGameEvent -> event
//            }
//        }
        TODO("return always an empty list, The command to get the list of events need to be implemented")
    }
}
