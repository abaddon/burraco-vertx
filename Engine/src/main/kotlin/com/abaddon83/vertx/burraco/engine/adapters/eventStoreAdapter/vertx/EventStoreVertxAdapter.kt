package com.abaddon83.vertx.burraco.engine.adapters.eventStoreAdapter.vertx

import com.abaddon83.utils.es.Event
import com.abaddon83.vertx.burraco.engine.adapters.eventStoreAdapter.vertx.model.ExtendEvent
import com.abaddon83.vertx.burraco.engine.events.BurracoGameEvent
import com.abaddon83.vertx.burraco.engine.ports.EventStorePort
import io.vertx.core.Handler
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.logging.LoggerFactory
import io.vertx.kotlin.coroutines.awaitResult
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


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
        val extendedEventList= EventsCache.getEventCached(pk)

        //extendedEventsLoaded.remove(pk)

        log.info(">> ${extendedEventList.size} extendedEvents available")
        return when {
            extendedEventList.isEmpty() -> {
                listOf()
            }
            else -> {
                extendedEventList.fold(listOf<BurracoGameEvent>()) { list, extendedEvent ->
                    when (val ev = extendedEvent.toEvent()) {
                        is BurracoGameEvent -> listOf(list, listOf<BurracoGameEvent>(ev)).flatten()
                        else -> list
                    }
                }
            }
        }.sortedBy { e -> e.created }.reversed()
    }

    fun loadExtendedEvents(pk: String,events: Set<ExtendEvent>){
        EventsCache.persist(pk,events)
    }

    fun getEvents(pk: String, entityName: String): Promise<Set<ExtendEvent>> {
            val done: Promise<Set<ExtendEvent>> = Promise.promise()
            EventStoreServiceVertxEBProxy(vertx, SERVICE_ADDRESS)
                .getEntityEvents(entityName, pk) { ar ->
                    if (ar.succeeded()) {
                        log.info("${ar.result().size} events loaded")

                        done.complete(ar.result())
                    } else {
                        log.error("future failed ${ar.cause()}")
                        done.fail(ar.cause())
                    }
                }
        return done;
    }

//    suspend fun nonAsyncMethod1(pk: String, entityName: String): Set<ExtendEvent> = suspendCoroutine { cont ->
//        EventStoreServiceVertxEBProxy(vertx, SERVICE_ADDRESS)
//            .getEntityEvents(entityName, pk) { ar ->
//                if (ar.succeeded()) {
//                    log.info("${ar.result().size} events loaded")
//                    cont.resume(ar.result())
//                } else {
//                    log.error("future failed ${ar.cause()}")
//                    cont.resumeWithException(ar.cause())
//                }
//            }
//
//    }
//
//    suspend fun nonAsyncMethod3(pk: String, entityName: String): Set<ExtendEvent> {
//        return awaitResult<Set<ExtendEvent>> { handler ->
//            EventStoreServiceVertxEBProxy(vertx, SERVICE_ADDRESS)
//                .getEntityEvents(entityName, pk, handler)
//        }
//    }


//    private fun getEvents2(pk: String, entityName: String): Set<ExtendEvent> {
//        var result: Set<ExtendEvent> = setOf()
//        val waitFor = CoroutineScope(Dispatchers.IO).async {
//            EventStoreServiceVertxEBProxy(vertx, SERVICE_ADDRESS)
//                .getEntityEvents(entityName, pk) { ar ->
//                    if (ar.succeeded()) {
//                        log.info("${ar.result().size} events loaded")
//                        result = ar.result().toSet()
//                    } else {
//                        log.error("future failed ${ar.cause()}")
//                        ar.result().toSet()
//                    }
//                }
//            return@async result
//        }
//
//        return result
//    }

}
