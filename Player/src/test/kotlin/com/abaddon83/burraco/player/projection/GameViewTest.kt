package com.abaddon83.burraco.player.projection

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.event.game.GameCreated
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

internal class GameViewTest {

    @Test
    fun `Given empty GameView when GameCreated event applied then new GameView created with correct values`() {
        val gameIdentity = GameIdentity.create()
        val gameCreatedEvent = GameCreated.create(gameIdentity)
        val emptyGameView = GameView.empty()

        val result = emptyGameView.applyEvent(gameCreatedEvent) as GameView

        assertEquals(GameViewKey(gameIdentity), result.key)
        assertTrue(result.players.isEmpty())
        assertEquals(GameState.DRAFT, result.state)
        assertNotNull(result.lastUpdated)
        assertTrue(result.lastUpdated!!.isAfter(Instant.now().minusSeconds(5)))
    }

    @Test
    fun `Given non-empty GameView when GameCreated event applied then throws exception`() {
        val gameIdentity1 = GameIdentity.create()
        val gameIdentity2 = GameIdentity.create()
        val gameCreatedEvent = GameCreated.create(gameIdentity2)
        
        val nonEmptyGameView = GameView(
            key = GameViewKey(gameIdentity1),
            players = emptyList(),
            state = GameState.DRAFT,
            lastProcessedEvent = ConcurrentHashMap(),
            lastUpdated = Instant.now()
        )

        assertThrows(IllegalStateException::class.java) {
            nonEmptyGameView.applyEvent(gameCreatedEvent)
        }
    }

    @Test
    fun `Given unsupported event when applied then throws exception`() {
        val emptyGameView = GameView.empty()
        val unsupportedEvent = object : io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent {
            override val messageId = java.util.UUID.randomUUID()
            override val header = io.github.abaddon.kcqrs.core.domain.messages.events.EventHeader.create("Test")
            override val aggregateId = GameIdentity.create()
            override val aggregateType = "Test"
            override val version = 1L
        }

        assertThrows(IllegalArgumentException::class.java) {
            emptyGameView.applyEvent(unsupportedEvent)
        }
    }

    @Test
    fun `Given GameView when withPosition called then position updated correctly`() {
        val gameIdentity = GameIdentity.create()
        val gameCreatedEvent = GameCreated.create(gameIdentity)
        val gameView = GameView.empty()
        val originalTime = gameView.lastUpdated

        val result = gameView.withPosition(gameCreatedEvent) as GameView

        assertEquals(gameView.key, result.key)
        assertEquals(gameView.players, result.players)
        assertEquals(gameView.state, result.state)
        assertTrue(result.lastProcessedEvent.containsKey(gameCreatedEvent.aggregateType))
        assertEquals(gameCreatedEvent.version, result.lastProcessedEvent[gameCreatedEvent.aggregateType])
        assertNotNull(result.lastUpdated)
        assertTrue(result.lastUpdated!!.isAfter(originalTime!!))
    }

    @Test
    fun `Given empty factory method when called then returns correct empty GameView`() {
        val emptyGameView = GameView.empty()

        assertEquals(GameViewKey(GameIdentity.empty()), emptyGameView.key)
        assertTrue(emptyGameView.players.isEmpty())
        assertEquals(GameState.DRAFT, emptyGameView.state)
        assertNotNull(emptyGameView.lastProcessedEvent)
        assertTrue(emptyGameView.lastProcessedEvent.isEmpty())
        assertNotNull(emptyGameView.lastUpdated)
    }

    @Test
    fun `Given GameView with existing position when withPosition called with same aggregateType then position updated`() {
        val gameIdentity = GameIdentity.create()
        val gameCreatedEvent1 = GameCreated.create(gameIdentity).copy(version = 1L)
        val gameCreatedEvent2 = GameCreated.create(gameIdentity).copy(version = 2L)
        
        val gameView = GameView.empty()
        val withFirstPosition = gameView.withPosition(gameCreatedEvent1) as GameView
        val withSecondPosition = withFirstPosition.withPosition(gameCreatedEvent2) as GameView

        assertEquals(1L, withFirstPosition.lastProcessedEvent[gameCreatedEvent1.aggregateType])
        assertEquals(2L, withSecondPosition.lastProcessedEvent[gameCreatedEvent2.aggregateType])
    }
}