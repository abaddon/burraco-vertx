//package com.abaddon83.burraco.game.adapters.eventStoreAdapter.tcp
//
//import com.abaddon83.utils.ddd.Event
//import com.abaddon83.burraco.common.events.BurracoGameEvent
//import com.abaddon83.burraco.common.events.ExtendEvent
//import com.abaddon83.burraco.game.adapters.eventStoreAdapter.tcp.config.EventStoreTcpBusConfig
//import com.abaddon83.burraco.game.ports.EventStorePort
//import io.vertx.core.Handler
//import io.vertx.core.Promise
//import io.vertx.core.Vertx
//import io.vertx.core.json.JsonObject
//import io.vertx.core.net.NetClient
//import io.vertx.ext.eventbus.bridge.tcp.impl.protocol.FrameHelper
//import io.vertx.ext.eventbus.bridge.tcp.impl.protocol.FrameParser
//import io.vertx.kotlin.coroutines.await
//
//import org.slf4j.LoggerFactory
//
//class EventStoreTcpBusAdapter(val vertx: Vertx, val config: EventStoreTcpBusConfig) : EventStorePort {
//    private val log = LoggerFactory.getLogger(this::class.qualifiedName)
//
//    override fun save(events: Iterable<Event>, handler: Handler<Boolean>) {
//        val client = vertx.createNetClient()
//        client.connect(config.service.port, config.service.address) { conn ->
//            if (conn.failed()) {
//                log.error("Connection failed", conn.cause())
//            } else {
//                val socket = conn.result()
//                socket
//                    .closeHandler { it ->
//                        client.close {
//                            if (it.failed()) { log.error("Client not closed") }
//                        }
//                    }
//                    .handler(response(handler,client))
//
//                //socket.close().onSuccess { log.info("socket closed") }
//
//                events.map { e ->
//                    val extendedEvent = ExtendEvent(e)
//
//                    FrameHelper.sendFrame(
//                        "send",
//                        config.channels.publish,
//                        "#backtrack",
//                        JsonObject.mapFrom(extendedEvent),
//                        socket
//                    )
////                    FrameHelper.sendFrame(
////                        "publish",
////                        SERVICE_ADDRESS_PUBLISH,
////                        JsonObject.mapFrom(extendedEvent),
////                        socket
////                    )
//                }
//            }
//        }
//    }
//
//    private fun response(handler: Handler<Boolean>, client: NetClient): FrameParser {
//       return  FrameParser{ parse ->
//            if(parse.succeeded()){
//                val frameBody = parse.result().getJsonObject("body")
//                handler.handle(frameBody.getBoolean("published"))
//            }else{
//                print(parse.cause())
//                handler.handle(false)
//            }
//            client.close()
//        }
//    }
//
//    override fun getBurracoGameEvents(pk: String): Promise<List<BurracoGameEvent>> {
//        val resultPromise: Promise<List<BurracoGameEvent>> = Promise.promise()
//        val client = vertx.createNetClient()
//        client.connect(config.service.port, config.service.address) { conn ->
//            if (conn.failed()) {
//                log.error("Connection failed", conn.cause())
//                resultPromise.fail(conn.cause())
//            } else {
//                val socket = conn.result()
//                //close handler
//                socket.closeHandler { it ->
//                    client.close {
//                        if (it.failed()) {
//                            log.error("Client not closed")
//                        }
//                    }
//                }
//                //socket handler
//                val parserHandler = FrameParser{ parse ->
//                    //val events = listOf<BurracoGameEvent>()
//                    if(parse.succeeded()){
//                        val frameBody = parse.result().getJsonObject("body")
//                        val eventsArray = frameBody.getJsonArray("events")
//                        val events = eventsArray.map { element ->
//                            VertxExtendEvent.from(element as JsonObject).toEvent() as BurracoGameEvent
//                        }.toList()
//                        resultPromise.complete(events)
//                    }else{
//                        print(parse.cause())
//                        resultPromise.fail(parse.cause())
//                    }
//                    client.close()
//                }
//
//                socket.handler(parserHandler)
//
//                //Message to send
//                FrameHelper.sendFrame(
//                    "send",
//                    config.channels.query,
//                    "#backtrack",
//                    JsonObject(mapOf("entityName" to "BurracoGame", "entityKey" to pk)),
//                    socket
//                )
//            }
//        }
//        return resultPromise
//    }
//}
