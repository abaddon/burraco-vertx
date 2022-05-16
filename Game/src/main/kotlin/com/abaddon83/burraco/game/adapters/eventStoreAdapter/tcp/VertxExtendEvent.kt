//package com.abaddon83.burraco.game.adapters.eventStoreAdapter.tcp
//
//import com.abaddon83.burraco.common.events.ExtendEvent
//import io.vertx.core.json.JsonObject
//import java.util.*
//
//class VertxExtendEvent{
//    companion object{
//        fun from(vertxJsonObject: JsonObject): ExtendEvent{
//            return ExtendEvent(
//                name = vertxJsonObject.getString("name"),
//                entityKey = UUID.fromString(vertxJsonObject.getString("entityKey")),
//                entityName = vertxJsonObject.getString("entityName"),
//                instant = vertxJsonObject.getInstant("instant"),
//                jsonPayload = vertxJsonObject.getString("jsonPayload")
//            )
//        }
//    }
//}
