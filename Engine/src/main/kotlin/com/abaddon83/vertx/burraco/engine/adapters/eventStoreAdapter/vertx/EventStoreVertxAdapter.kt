package com.abaddon83.vertx.burraco.engine.adapters.eventStoreAdapter.vertx

import com.abaddon83.utils.es.Event
import com.abaddon83.vertx.burraco.engine.adapters.eventStoreAdapter.vertx.model.ExtendEvent
import com.abaddon83.vertx.burraco.engine.events.BurracoGameEvent
import com.abaddon83.vertx.burraco.engine.ports.EventStorePort
import io.vertx.core.*
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
        val result= getEvents(pk,"BurracoGame").future().result()
        return listOf<BurracoGameEvent>()
        //result.ifEmpty { return listOf<BurracoGameEvent>() }

        return result.fold(listOf<BurracoGameEvent>()) { list, extendedEvent ->
            when (val ev = extendedEvent.toEvent()) {
                is BurracoGameEvent -> listOf(list, listOf<BurracoGameEvent>(ev)).flatten()
                else -> list
            }
        }
    }

    private fun getEvents(pk: String, entityName: String): Promise<Set<ExtendEvent>> {
        val done = Promise.promise<Set<ExtendEvent>>()
        EventStoreServiceVertxEBProxy(vertx, SERVICE_ADDRESS)
            .getEntityEvents(entityName, pk) { ar ->
                if (ar.succeeded()) {
                    log.info("future done ${ar.result()}")
                    done.complete(ar.result())
                }else{
                    log.error("future failed ${ar.cause()}")
                    done.fail("Get event failed")
                }
            }
        return done
    }
}
