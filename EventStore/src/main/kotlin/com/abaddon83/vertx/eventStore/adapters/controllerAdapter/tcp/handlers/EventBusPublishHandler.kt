package com.abaddon83.vertx.eventStore.adapters.controllerAdapter.tcp.handlers

import com.abaddon83.utils.functionals.Invalid
import com.abaddon83.utils.functionals.Valid
import com.abaddon83.vertx.eventStore.models.Event
import com.abaddon83.vertx.eventStore.ports.ControllerPort
import com.abaddon83.vertx.eventStore.ports.Outcome
import io.vertx.core.Handler
import io.vertx.core.MultiMap
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject
import org.slf4j.LoggerFactory

class EventBusPublishHandler(val controller: ControllerPort): Handler<Message<JsonObject>> {
    val log = LoggerFactory.getLogger(this::class.java)

    override fun handle(message: Message<JsonObject>?) {
        checkNotNull(message){"Event received null"}
        when(val result = publishMessage(message.headers(),message.body())){
            is Valid -> {
                log.info("published")
                message.reply(JsonObject(mapOf("published" to true)))
            }
            is Invalid ->{
                log.error("Not published: {}", result.err.msg)
                message.reply(JsonObject(mapOf("published" to false)))
            }
        }
    }

    private fun publishMessage(headers: MultiMap, body: JsonObject): Outcome{
        val event = Event(body)
        return controller.persist(event)
    }
}