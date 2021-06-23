package com.abaddon83.eventStore.adapters.controllerAdapter.tcp

import com.abaddon83.burraco.common.events.BurracoGameCreated
import com.abaddon83.burraco.common.events.BurracoGameEvent
import com.abaddon83.burraco.common.events.ExtendEvent
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.eventStore.ServiceConfig
import com.abaddon83.eventStore.adapters.repositoryAdapter.mysql.MysqlRepositoryAdapter
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.eventbus.bridge.tcp.impl.protocol.FrameHelper
import io.vertx.ext.eventbus.bridge.tcp.impl.protocol.FrameParser
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(VertxExtension::class)
internal class EventStoreServiceTCPVerticleTest{
    companion object {
        private val vertx: Vertx = Vertx.vertx();
        private val serviceConfig = ServiceConfig.load()

        @BeforeAll
        @JvmStatic
        fun beforeAll(context: VertxTestContext) {

            val eventStoreServiceTCPVerticle = EventStoreServiceTCPVerticle(ControllerAdapter(MysqlRepositoryAdapter(serviceConfig.mysqlRepository)),eventStoreConfig = serviceConfig.eventStoreServiceTCP)
            vertx.deployVerticle(eventStoreServiceTCPVerticle)
                .onFailure(context::failNow)
                .onSuccess{
                    context.completeNow()
                }
        }
    }


    @Test
    fun `publish`(context: VertxTestContext){
        val client = vertx.createNetClient()
        client.connect(7000,"localhost"){conn ->
            if(conn.failed()){
                context.failNow(conn.cause())
            }else{
                val socket = conn.result()
                val parser = FrameParser{ parse ->
                    if(parse.succeeded()){
                        val frame = parse.result()
                        println(frame.map)
                        context.completeNow()
                    }else{
                        print(parse.cause())
                        context.failNow(parse.cause())
                    }
                    client.close()
                }
                socket.handler(parser)

                val extendedEvent = ExtendEvent(BurracoGameCreated(GameIdentity.create()))

                FrameHelper.sendFrame("send",
                    serviceConfig.eventStoreServiceTCP.channels.publish, "#backtrack", JsonObject.mapFrom(extendedEvent), socket)
                //context.completeNow()
            }
        }

    }

    @Test
    fun `query`(context: VertxTestContext){
        val client = vertx.createNetClient()
        client.connect(7000,"localhost"){conn ->
            if(conn.failed()){
                context.failNow(conn.cause())
            }else{
                val socket = conn.result()
                val parser = FrameParser{ parse ->
                    if(parse.succeeded()){
                        val frameBody = parse.result().getJsonObject("body")
                        val eventsArray = frameBody.getJsonArray("events")
                        val events = eventsArray.map { element ->
                            convertJSonVertxTo(element as JsonObject).toEvent() as BurracoGameEvent
                        }.toList()
                            assertEquals(1,events.size)

                        context.completeNow()
                    }else{
                        print(parse.cause())
                        context.failNow(parse.cause())
                    }
                    client.close()
                }
                socket.handler(parser)

                val extendedEvent = ExtendEvent(BurracoGameCreated(GameIdentity.create()))

                FrameHelper.sendFrame(
                    "send",
                    serviceConfig.eventStoreServiceTCP.channels.query,
                    "#backtrack",
                    JsonObject(mapOf(
                        "entityName" to "BurracoGame",
                        "entityKey" to "841f17ed-49b1-429f-ad27-884d289ee722"
                    )),
                    socket
                )
                //context.completeNow()
            }
        }

    }

    private fun convertJSonVertxTo(vertxJsonObject: JsonObject): ExtendEvent{
        return ExtendEvent(
            name = vertxJsonObject.getString("name"),
            entityKey = UUID.fromString(vertxJsonObject.getString("entityKey")),
            entityName = vertxJsonObject.getString("entityName"),
            instant = vertxJsonObject.getInstant("instant"),
            jsonPayload = vertxJsonObject.getString("jsonPayload")
        )
    }
}