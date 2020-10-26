package com.abaddon83.vertx.burraco.engine.adapters.eventStoreAdapter.vertx

import com.abaddon83.utils.es.Event
import com.abaddon83.vertx.burraco.engine.adapters.eventStoreAdapter.vertx.model.ExtendEvent
import com.abaddon83.vertx.burraco.engine.events.BurracoGameEvent
import com.abaddon83.vertx.burraco.engine.ports.EventStorePort
import io.vertx.core.*
import io.vertx.core.logging.LoggerFactory
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext


class EventStoreVertxAdapter(vertx: Vertx) : EventStorePort() {
    private val vertx: Vertx = vertx
    val scope: CoroutineContext = Dispatchers.IO


    companion object {
        const val SERVICE_ADDRESS = "eventstore-bus-service-address"
        private val log = LoggerFactory.getLogger(this::class.qualifiedName)!!
    }

    override fun save(events: Iterable<Event>) {
        val handler = Handler<AsyncResult<Boolean>> { ar ->
            if (ar != null){
                if (ar.succeeded()) {
                    System.out.println("event stored: ${ar.result()}")
                } else {
                    System.out.println("event NOT stored: ${ar.cause()}")
                }
            }
        }


        events.forEach { event ->
            EventStoreServiceVertxEBProxy(vertx, SERVICE_ADDRESS)
                .persist(ExtendEvent(event), handler)
        }
    }

    override fun getBurracoGameEvents(pk: String): List<BurracoGameEvent> {
        var result: List<BurracoGameEvent> = listOf()

        val job = GlobalScope.launch(scope) {
            result = burracoEvents(pk)
        }
        runBlocking {
            job.join()
        }
        return result
    }

    private fun eventsInFuture(pk: String): Future<Set<ExtendEvent>> {
        val done: Promise<Set<ExtendEvent>> = Promise.promise()

        val handler = Handler<AsyncResult<Set<ExtendEvent>>> { event ->
                if (event != null) {
                    if (event.succeeded()) {
                        done.complete(event.result())
                    } else {
                        done.fail(event.cause().message)
                    }
                }
            }

        EventStoreServiceVertxEBProxy(vertx, SERVICE_ADDRESS)
            .getEntityEvents("BurracoGame", pk, handler)

        return done.future()
    }

    private suspend fun awaitingEvents(pk: String): Set<ExtendEvent> {
        val future = eventsInFuture(pk)
        return future.await()

    }

    private suspend fun burracoEvents(pk: String): List<BurracoGameEvent> {
        val eventSet = awaitingEvents(pk)

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



}
