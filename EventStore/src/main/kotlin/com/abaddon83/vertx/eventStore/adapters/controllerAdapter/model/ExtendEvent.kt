package com.abaddon83.vertx.eventStore.adapters.controllerAdapter.model

import com.abaddon83.utils.eventStore.model.Event
import io.vertx.codegen.annotations.DataObject
import io.vertx.core.json.JsonObject
import java.time.Instant
import java.util.*

@DataObject
class ExtendEvent(
    name: String, entityKey: UUID, entityName: String, instant: Instant, jsonPayload: String
): Event(
    name,
    entityKey,
    entityName,
    instant,
    jsonPayload)
 {
    constructor(json: JsonObject): this(json.mapTo(ExtendEvent::class.java))

     constructor(ev: ExtendEvent): this(ev.name,ev.entityKey, ev.entityName,ev.instant, ev.jsonPayload)
     constructor(ev: Event): this(ev.name,ev.entityKey, ev.entityName,ev.instant, ev.jsonPayload)

    fun toJson(): JsonObject {
        return JsonObject.mapFrom(this)
    }

}