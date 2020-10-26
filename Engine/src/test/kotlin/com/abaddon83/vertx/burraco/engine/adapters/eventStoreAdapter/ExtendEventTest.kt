package com.abaddon83.vertx.burraco.engine.adapters.eventStoreAdapter

import com.abaddon83.vertx.burraco.engine.adapters.commandController.models.Suit
import com.abaddon83.vertx.burraco.engine.adapters.eventStoreAdapter.vertx.model.ExtendEvent
import com.abaddon83.vertx.burraco.engine.events.BurracoGameCreated
import com.abaddon83.vertx.burraco.engine.models.decks.Card
import com.abaddon83.vertx.burraco.engine.models.decks.Ranks
import com.abaddon83.vertx.burraco.engine.models.decks.Suits
import com.abaddon83.vertx.burraco.engine.models.games.GameIdentity
import io.vertx.core.json.JsonObject
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Test
import java.time.Instant
import java.util.*
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

    @Test
    fun `Given a json string I want to convert in the right Event model`(){
        //Json.decodeFromString<ExtendEvent>()

        val burracoGameCreatedEv= BurracoGameCreated(GameIdentity.create(), listOf(Card(Suits.Clover,Ranks.Ace),Card(Suits.Clover,Ranks.Eight)))
        val extendedEvent = ExtendEvent(burracoGameCreatedEv)

        val event= extendedEvent.toEvent()
        assertEquals(burracoGameCreatedEv,event)

    }
}