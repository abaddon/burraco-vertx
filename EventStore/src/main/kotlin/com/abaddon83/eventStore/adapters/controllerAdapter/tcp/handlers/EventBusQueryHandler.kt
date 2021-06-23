package com.abaddon83.eventStore.adapters.controllerAdapter.tcp.handlers

import com.abaddon83.eventStore.models.Event
import com.abaddon83.eventStore.ports.ControllerPort
import io.vertx.core.Handler
import io.vertx.core.MultiMap
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject
import org.slf4j.LoggerFactory

class EventBusQueryHandler(val controller: ControllerPort): Handler<Message<JsonObject>> {
    val log = LoggerFactory.getLogger(this::class.java)

    override fun handle(message: Message<JsonObject>?) {
        checkNotNull(message){"Event received null"}
        val jsonObject = message.body()
        val events=QueryMessage(message.headers(), jsonObject)
        val jsonEvents = events.map { event ->  JsonObject.mapFrom(event) }.toList()
        val response = JsonObject.mapFrom(mapOf("events" to jsonEvents))
        message.reply(response)

    }

    private fun QueryMessage(headers: MultiMap, body: JsonObject): List<Event>{
        val entityName = body.getString("entityName")
        val entityKey = body.getString("entityKey")
        return controller.getEntityEvents(entityName = entityName,entityKey = entityKey)
    }
}