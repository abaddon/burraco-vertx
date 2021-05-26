package com.abaddon83.vertx.burraco.game.adapters.eventStoreAdapter.tcp

import com.abaddon83.utils.ddd.Event
import com.abaddon83.burraco.common.events.BurracoGameEvent
import com.abaddon83.burraco.common.events.ExtendEvent
import com.abaddon83.vertx.burraco.game.ports.EventStorePort
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.eventbus.bridge.tcp.impl.protocol.FrameHelper
import io.vertx.ext.eventbus.bridge.tcp.impl.protocol.FrameParser
import io.vertx.kotlin.coroutines.await

import org.slf4j.LoggerFactory

class EventStoreTcpAdapter(val vertx: Vertx) : EventStorePort() {
    private val log = LoggerFactory.getLogger(this::class.qualifiedName)

    companion object {
        const val SERVICE_TCP_ADDRESS = "localhost"
        const val SERVICE_TCP_PORT = 7000
        const val SERVICE_ADDRESS_PUBLISH = "eventstore-bus-publish"
        const val SERVICE_ADDRESS_QUERY = "eventstore-bus-query"
    }

    override fun save(events: Iterable<Event>) {
        val client = vertx.createNetClient()
        client.connect(SERVICE_TCP_PORT, SERVICE_TCP_ADDRESS) { conn ->
            if (conn.failed()) {
                log.error("Connection failed", conn.cause())
            } else {
                val socket = conn.result()

                socket.closeHandler { it ->
                    client.close {
                        if (it.failed()) {
                            log.error("Client not closed")
                        }
                    }
                }

                events.map { e ->
                    val extendedEvent = ExtendEvent(e)

                    FrameHelper.sendFrame(
                        "publish",
                        SERVICE_ADDRESS_PUBLISH,
                        JsonObject.mapFrom(extendedEvent),
                        socket
                    )
                }
                socket.close()
                    .onSuccess {
                        log.info("socket closed")
                    }
            }
        }
    }

//    override fun getBurracoGameEvents(pk: String): List<BurracoGameEvent> {
//
//    }

//    override suspend fun getBurracoGameEvents(pk: String): List<BurracoGameEvent> {
//            return getBurracoGameEventsAsync(pk).future().await()
//    }

    override fun getBurracoGameEvents(pk: String): Promise<List<BurracoGameEvent>> {
        val resultPromise: Promise<List<BurracoGameEvent>> = Promise.promise()
        val client = vertx.createNetClient()
        client.connect(SERVICE_TCP_PORT, SERVICE_TCP_ADDRESS) { conn ->
            if (conn.failed()) {
                log.error("Connection failed", conn.cause())
                resultPromise.fail(conn.cause())
            } else {
                val socket = conn.result()
                //close handler
                socket.closeHandler { it ->
                    client.close {
                        if (it.failed()) {
                            log.error("Client not closed")
                        }
                    }
                }
                //socket handler
                val parserHandler = FrameParser{ parse ->
                    //val events = listOf<BurracoGameEvent>()
                    if(parse.succeeded()){
                        val frameBody = parse.result().getJsonObject("body")
                        val eventsArray = frameBody.getJsonArray("events")
                        val events = eventsArray.map { element ->
                            VertxExtendEvent.from(element as JsonObject).toEvent() as BurracoGameEvent
                        }.toList()
                        resultPromise.complete(events)
                    }else{
                        print(parse.cause())
                        resultPromise.fail(parse.cause())
                    }
                    client.close()
                }

                socket.handler(parserHandler)

                //Message to send
                FrameHelper.sendFrame(
                    "send",
                    SERVICE_ADDRESS_QUERY,
                    "#backtrack",
                    JsonObject(mapOf("entityName" to "BurracoGame", "entityKey" to pk)),
                    socket
                )
            }
        }
        return resultPromise
    }
}
