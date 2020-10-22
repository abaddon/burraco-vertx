package com.abaddon83.vertx.burraco.engine.adapters.eventStoreAdapter.vertx

import com.abaddon83.utils.es.Event
import com.abaddon83.vertx.burraco.engine.adapters.eventStoreAdapter.vertx.model.ExtendEvent
import com.abaddon83.vertx.burraco.engine.events.BurracoGameEvent
import com.abaddon83.vertx.burraco.engine.ports.EventStorePort
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.logging.LoggerFactory
import io.vertx.kotlin.core.onCompleteAwait
import io.vertx.kotlin.coroutines.awaitResult
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.xml.bind.JAXBElement
import kotlin.concurrent.thread
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
        val promiseEventSet: Promise<Set<ExtendEvent>> = getEvents("pk", "BurracoGame")
        promiseEventSet.future()
        val eventSet = listOf<ExtendEvent>()

        log.info(">> ${eventSet!!.size} events loaded")
        return when {
            eventSet!!.isEmpty() -> listOf()
            else -> {
                eventSet!!.fold(listOf<BurracoGameEvent>()) { list, extendedEvent ->
                    when (val ev = extendedEvent.toEvent()) {
                        is BurracoGameEvent -> listOf(list, listOf<BurracoGameEvent>(ev)).flatten()
                        else -> list
                    }
                }
            }
        }
    }

    private fun getEvents(pk: String, entityName: String): Promise<Set<ExtendEvent>> {
        val done: Promise<Set<ExtendEvent>> = Promise.promise()
        EventStoreServiceVertxEBProxy(vertx, SERVICE_ADDRESS)
            .getEntityEvents(entityName, pk) { ar ->
                if (ar.succeeded()) {
                    log.info("${ar.result().size} events loaded")
                    return@getEntityEvents done.complete(ar.result())
                } else {
                    log.error("future failed ${ar.cause()}")
                    done.fail("Get event failed")
                }
            }
        return done
    }

    suspend fun nonAsyncMethod1(pk: String, entityName: String): Set<ExtendEvent> = suspendCoroutine { cont ->
        EventStoreServiceVertxEBProxy(vertx, SERVICE_ADDRESS)
            .getEntityEvents(entityName, pk) { ar ->
                if (ar.succeeded()) {
                    log.info("${ar.result().size} events loaded")
                    cont.resume(ar.result())
                } else {
                    log.error("future failed ${ar.cause()}")
                    cont.resumeWithException(ar.cause())
                }
            }

    }

    suspend fun nonAsyncMethod3(pk: String, entityName: String): Set<ExtendEvent> {
        return awaitResult<Set<ExtendEvent>> { handler ->
            EventStoreServiceVertxEBProxy(vertx, SERVICE_ADDRESS)
                .getEntityEvents(entityName, pk, handler)
        }
    }


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
