package com.abaddon83.vertx.burraco.engine.adapters.eventStoreAdapter

import com.abaddon83.vertx.burraco.engine.adapters.eventStoreAdapter.vertx.model.ExtendEvent
import io.vertx.core.json.JsonObject
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.assertEquals

class ExtendEventTest {

    @Test
    fun `Given a json string I want to convert in the ExtendedEvent model`(){
        //Json.decodeFromString<ExtendEvent>()
        val json: JsonObject = JsonObject("{\"name\":\"BurracoGameCreated\",\"entityKey\":\"ae2da3c3-48a8-4b2f-99c1-669fb52e700e\",\"entityName\":\"BurracoGame\",\"instant\":\"2020-10-20T08:38:53Z\",\"jsonPayload\":\"{}\"}")
//        println(json.getString("name"))
//        println(json.getString("entityKey"))
//        println(json.getInstant("instant"))
//        println(json.getString("jsonPayload"))
        val event = ExtendEvent(json)
        assertEquals(event.name, "BurracoGameCreated")
    }
}