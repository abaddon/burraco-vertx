package com.abaddon83.burraco.game.adapters.gameEventPublisher.kafka

import com.abaddon83.burraco.game.events.game.CardAddedDeck
import com.abaddon83.burraco.game.events.game.CardDealingRequested
import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.game.GameIdentity
import com.abaddon83.burraco.game.models.player.PlayerIdentity
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class KafkaEventTest {

    @Test
    fun `Given a CardDealingRequested event, when converted, then new instance of KafkaEvent created`() {
        val gameIdentity = GameIdentity.create()
        val playerIdentity = PlayerIdentity.create()
        val event = CardDealingRequested.create(gameIdentity, playerIdentity)
        val kafkaEvent = KafkaEvent.from(event)
        assertEquals(CardDealingRequested::class.java.simpleName, kafkaEvent?.eventName)
        assertEquals("GameService", kafkaEvent?.eventOwner)
        assertTrue(kafkaEvent?.eventPayload!!.contains(playerIdentity.valueAsString()))
        assertTrue(kafkaEvent?.eventPayload!!.contains(gameIdentity.valueAsString()))
    }

    @Test
    fun `Given a CardAddedDeck event, when converted, then new instance of KafkaEvent is null`() {
        val gameIdentity = GameIdentity.create()
        val card = Card.jolly()
        val event = CardAddedDeck.create(gameIdentity, card)
        val kafkaEvent = KafkaEvent.from(event)
        assertNull(kafkaEvent)
    }

    @Test
    fun `Given a valid KafkaEvent, when execute toJson, then json created`() {
        val gameIdentity = GameIdentity.create()
        val playerIdentity = PlayerIdentity.create()
        val event = CardDealingRequested.create(gameIdentity, playerIdentity)
        val kafkaEvent = KafkaEvent.from(event)
        val json = kafkaEvent?.toJson()
        assertTrue(json!!.contains(playerIdentity.valueAsString()))
        assertTrue(json!!.contains(gameIdentity.valueAsString()))
    }
}