package com.abaddon83.vertx.eventStore.adapters.controllerAdapter.tcp.handlers

import com.abaddon83.vertx.eventStore.models.Event
import com.abaddon83.vertx.eventStore.ports.ControllerPort
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
        log.warn("Msg type ${jsonObject.getString("type")}")
        val events=QueryMessage(message.headers(), jsonObject)
//        val event1 = JsonObject("{\"name\":\"BurracoGameCreated\",\"entityKey\":\"c3a7d61d-0d3f-4cf9-ba17-402da1436bec\",\"entityName\":\"BurracoGame\",\"instant\":\"2021-05-26T07:47:10.864033Z\",\"jsonPayload\":\"{\\\"entityName\\\":\\\"BurracoGame\\\",\\\"identity\\\":{\\\"id\\\":\\\"c3a7d61d-0d3f-4cf9-ba17-402da1436bec\\\"}}\"}")
//        val event2 = JsonObject("{\"name\":\"BurracoGameCreated\",\"entityKey\":\"c3a7d61d-0d3f-4cf9-ba17-402da1436bec\",\"entityName\":\"BurracoGame\",\"instant\":\"2021-05-26T07:47:10.864033Z\",\"jsonPayload\":\"{\\\"entityName\\\":\\\"BurracoGame\\\",\\\"identity\\\":{\\\"id\\\":\\\"c3a7d61d-0d3f-4cf9-ba17-402da1436bec\\\"}}\"}")
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