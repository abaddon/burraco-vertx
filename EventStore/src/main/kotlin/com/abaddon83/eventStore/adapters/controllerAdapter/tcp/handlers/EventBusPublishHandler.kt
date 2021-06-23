package com.abaddon83.eventStore.adapters.controllerAdapter.tcp.handlers

import com.abaddon83.utils.functionals.Invalid
import com.abaddon83.utils.functionals.Valid
import com.abaddon83.eventStore.models.Event
import com.abaddon83.eventStore.ports.ControllerPort
import com.abaddon83.eventStore.ports.Outcome
import io.vertx.core.Handler
import io.vertx.core.MultiMap
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject
import org.slf4j.LoggerFactory

class EventBusPublishHandler(val controller: ControllerPort): Handler<Message<JsonObject>> {
    val log = LoggerFactory.getLogger(this::class.java)

    override fun handle(message: Message<JsonObject>?) {
        checkNotNull(message){"Event received is null"}
        when(val result = persistMessage(message)){
            is Valid -> {
                log.info("Event ${Event(message.body()).name} persisted")
                message.reply(JsonObject(mapOf("published" to true)))
            }
            is Invalid ->{
                log.error("Event ${Event(message.body()).name} not persisted", result.err.msg)
                message.reply(JsonObject(mapOf("published" to false)))
            }
        }
    }

    private fun persistMessage(message: Message<JsonObject>): Outcome{
        val headers: MultiMap =  message.headers()
        val body: JsonObject = message.body()
        val event = Event(body)
        return controller.persist(event)
    }
}