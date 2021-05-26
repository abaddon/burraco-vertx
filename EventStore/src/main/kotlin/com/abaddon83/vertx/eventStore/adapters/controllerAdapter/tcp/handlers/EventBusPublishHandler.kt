package com.abaddon83.vertx.eventStore.adapters.controllerAdapter.tcp.handlers

import com.abaddon83.burraco.common.events.ExtendEvent
import com.abaddon83.utils.functionals.Invalid
import com.abaddon83.utils.functionals.Valid
import com.abaddon83.vertx.eventStore.models.Event
import com.abaddon83.vertx.eventStore.ports.ControllerPort
import io.vertx.core.Handler
import io.vertx.core.MultiMap
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject
import org.slf4j.LoggerFactory

class EventBusPublishHandler(val controller: ControllerPort): Handler<Message<JsonObject>> {
    val log = LoggerFactory.getLogger(this::class.java)

    override fun handle(event: Message<JsonObject>?) {
        checkNotNull(event){"Event received null"}
        publishMessage(event.headers(),event.body())
    }

    private fun publishMessage(headers: MultiMap, body: JsonObject){
        val event = Event(body)
        when(val result = controller.persist(event)){
            is Valid -> log.info("published")
            is Invalid -> log.error("Not published: {}", result.err.msg)
        }
    }
}