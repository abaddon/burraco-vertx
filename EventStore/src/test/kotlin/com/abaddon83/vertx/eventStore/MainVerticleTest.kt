package com.abaddon83.vertx.eventStore

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.bridge.BridgeOptions
import io.vertx.ext.bridge.PermittedOptions
import io.vertx.ext.eventbus.bridge.tcp.TcpEventBusBridge
import io.vertx.ext.eventbus.bridge.tcp.impl.protocol.FrameHelper
import io.vertx.ext.eventbus.bridge.tcp.impl.protocol.FrameParser
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith


@ExtendWith(VertxExtension::class)
class MainVerticleTest{
    companion object{
        private val vertx: Vertx = Vertx.vertx();
        @BeforeAll
        @JvmStatic
        fun beforeAll(context: VertxTestContext) {

            vertx.eventBus().consumer<JsonObject>("hello"){ handler ->
                println("msg_received:"+handler.body().toString())
                handler.reply(JsonObject(mapOf("value" to "Hello "+handler.body().getString("value"))))
            }
            val bridge = TcpEventBusBridge.create(vertx,
                BridgeOptions()
                    .addInboundPermitted(PermittedOptions())
                    .addOutboundPermitted(PermittedOptions())
            )
            bridge.listen(7000){res ->
                if(res.failed()){
                    context.failNow(res.cause())
                }else{
                    context.completeNow()
                }

            }
        }
    }


    @Test
    fun `test`(context: VertxTestContext) {
        assert(true)
        context.completeNow()
    }

    @Test
    fun `sendMsg`(context: VertxTestContext) {
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

                FrameHelper.sendFrame("send","hello", "#backtrack", JsonObject(mapOf("value" to "ciao")), socket)
            }
        }
    }
}

