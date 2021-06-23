package com.abaddon83.burraco.game.adapters.eventStoreAdapter

import com.abaddon83.burraco.common.events.ExtendEvent
import com.abaddon83.burraco.common.events.BurracoGameCreated
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.game.adapters.eventStoreAdapter.tcp.VertxExtendEvent
import io.vertx.core.json.JsonObject
import org.junit.jupiter.api.Test
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
        val event = VertxExtendEvent.from(json)
        assertEquals(event.name, "BurracoGameCreated")
    }

    @Test
    fun `Given a json string I want to convert in the right Event model`(){
        //Json.decodeFromString<ExtendEvent>()

        val burracoGameCreatedEv= BurracoGameCreated(GameIdentity.create())
        val extendedEvent = ExtendEvent(burracoGameCreatedEv)

        val event= extendedEvent.toEvent()
        assertEquals(burracoGameCreatedEv,event)

    }
}