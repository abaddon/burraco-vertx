package com.abaddon83.vertx.burraco.game.adapters.eventStoreAdapter.tcp

import com.abaddon83.burraco.common.events.BurracoGameCreated
import com.abaddon83.burraco.common.models.identities.GameIdentity
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.bridge.BridgeOptions
import io.vertx.ext.bridge.PermittedOptions
import io.vertx.ext.eventbus.bridge.tcp.TcpEventBusBridge
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
internal class EventStoreTcpAdapterTest {
    //private val vertx: Vertx = Vertx.vertx();

    companion object {
        private val vertx: Vertx = Vertx.vertx();

        @BeforeAll
        @JvmStatic
        fun beforeAll(context: VertxTestContext) {

            vertx.eventBus().consumer<JsonObject>(EventStoreTcpBusAdapter.SERVICE_BUS_ADDRESS) { handler ->
                println("msg_received:" + handler.body().toString())
                val jsonBody = handler.body()
                val key = jsonBody.getString("entityKey")
                val event1 = JsonObject("{\"name\":\"BurracoGameCreated\",\"entityKey\":\"c3a7d61d-0d3f-4cf9-ba17-402da1436bec\",\"entityName\":\"BurracoGame\",\"instant\":\"2021-05-26T07:47:10.864033Z\",\"jsonPayload\":\"{\\\"entityName\\\":\\\"BurracoGame\\\",\\\"identity\\\":{\\\"id\\\":\\\"c3a7d61d-0d3f-4cf9-ba17-402da1436bec\\\"}}\"}")
                val event2 = JsonObject("{\"name\":\"BurracoGameCreated\",\"entityKey\":\"c3a7d61d-0d3f-4cf9-ba17-402da1436bec\",\"entityName\":\"BurracoGame\",\"instant\":\"2021-05-26T07:47:10.864033Z\",\"jsonPayload\":\"{\\\"entityName\\\":\\\"BurracoGame\\\",\\\"identity\\\":{\\\"id\\\":\\\"c3a7d61d-0d3f-4cf9-ba17-402da1436bec\\\"}}\"}")
                val response = JsonObject.mapFrom(mapOf("events" to listOf(event1,event2)))
                handler.reply(response)
//                when {
//                    jsonBody.getString("type").equals("publish") -> {
//                        assertEquals("BurracoGameCreated", jsonBody.getString("name"))
//                    }
//                    jsonBody.getString("type").equals("send") -> {
//                        val key = jsonBody.getString("entityKey")
//                        val response = JsonObject("{\"name\":\"BurracoGameCreated\",\"entityKey\":\"c3a7d61d-0d3f-4cf9-ba17-402da1436bec\",\"entityName\":\"BurracoGame\",\"instant\":\"2021-05-26T07:47:10.864033Z\",\"jsonPayload\":\"{\\\"entityName\\\":\\\"BurracoGame\\\",\\\"identity\\\":{\\\"id\\\":\\\"c3a7d61d-0d3f-4cf9-ba17-402da1436bec\\\"}}\"}")
//                        handler.reply(response)
//                    }
//                    else -> {
//                        context.failNow("type not recognised")
//                    }
//                }
                //assertEquals("BurracoGameCreated", handler.body().getString("name"))
                //handler.reply(JsonObject(mapOf("value" to "Hello "+handler.body().getString("value"))))
                context.completeNow()
            }
            val bridge = TcpEventBusBridge.create(
                vertx,
                BridgeOptions()
                    .addInboundPermitted(PermittedOptions())
                    .addOutboundPermitted(PermittedOptions())
            )
            bridge.listen(EventStoreTcpBusAdapter.SERVICE_TCP_PORT) { res ->
                if (res.failed()) {
                    context.failNow(res.cause())
                } else {
                    context.completeNow()
                }
            }
        }
    }

    //@Test
    fun `testSave`(context: VertxTestContext) {
        EventStoreTcpBusAdapter.SERVICE_BUS_ADDRESS
        val eventStoreTcpAdapter = EventStoreTcpBusAdapter(vertx)
        val event = BurracoGameCreated(GameIdentity.create())
        eventStoreTcpAdapter.save(listOf(event))
        context.completeNow()
    }

    @Test
    fun `testQuery`(context: VertxTestContext) {
        EventStoreTcpBusAdapter.SERVICE_BUS_ADDRESS
        val eventStoreTcpAdapter = EventStoreTcpBusAdapter(vertx)
        val key = "c3a7d61d-0d3f-4cf9-ba17-402da1436bec"

        eventStoreTcpAdapter.getBurracoGameEvent(key).future()
            .onSuccess { list ->
                assertEquals(2, list.size)
                context.completeNow()
            }
            .onFailure(context::failNow)
    }
}