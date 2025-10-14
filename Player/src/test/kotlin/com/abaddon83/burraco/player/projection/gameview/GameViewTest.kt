package com.abaddon83.burraco.player.projection.gameview

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.event.game.GameCreated
import com.abaddon83.burraco.common.models.event.game.PlayerAdded
import com.abaddon83.burraco.player.projection.GameState
import io.github.abaddon.kcqrs.core.domain.messages.events.EventHeader
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.UUID
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
        assertEquals(4, result.maxPlayers)
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
            maxPlayers = 4,
            state = GameState.DRAFT,
            lastProcessedEvent = ConcurrentHashMap(),
            lastUpdated = Instant.now()
        )

        assertThrows(IllegalStateException::class.java) {
            nonEmptyGameView.applyEvent(gameCreatedEvent)
        }
    }

//    @Test
//    fun `Given unsupported event when applied then throws exception`() {
//        val emptyGameView = GameView.empty()
//        val unsupportedEvent = object : IDomainEvent {
//            override val messageId = UUID.randomUUID()
//            override val header = EventHeader.create("Test")
//            override val aggregateId = GameIdentity.create()
//            override val aggregateType = "Test"
//            override val version = 1L
//        }
//
//        assertThrows(IllegalArgumentException::class.java) {
//            emptyGameView.applyEvent(unsupportedEvent)
//        }
//    }

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
        val gameCreatedEvent1 = GameCreated.create(gameIdentity)
        val gameCreatedEvent2 = GameCreated.create(gameIdentity)

        val gameView = GameView.empty()
        val withFirstPosition = gameView.withPosition(gameCreatedEvent1) as GameView
        val withSecondPosition = withFirstPosition.withPosition(gameCreatedEvent2) as GameView

        assertEquals(1L, withFirstPosition.lastProcessedEvent[gameCreatedEvent1.aggregateType])
        assertEquals(1L, withSecondPosition.lastProcessedEvent[gameCreatedEvent2.aggregateType])
    }

    // ============ NEW TESTS FOR PLAYERADDED EVENT ============

    @Test
    fun `Given GameView when PlayerAdded event applied then player added to list`() {
        val gameIdentity = GameIdentity.create()
        val playerIdentity = PlayerIdentity.create()
        val gameCreatedEvent = GameCreated.create(gameIdentity)
        val playerAddedEvent = PlayerAdded.create(gameIdentity, playerIdentity)

        val gameView = GameView.empty().applyEvent(gameCreatedEvent) as GameView
        val result = gameView.applyEvent(playerAddedEvent) as GameView

        assertEquals(1, result.players.size)
        assertTrue(result.players.contains(playerIdentity))
        assertNotNull(result.lastUpdated)
    }

    @Test
    fun `Given GameView with players when PlayerAdded event applied then player added to existing list`() {
        val gameIdentity = GameIdentity.create()
        val player1 = PlayerIdentity.create()
        val player2 = PlayerIdentity.create()
        val player3 = PlayerIdentity.create()

        val gameView = GameView.empty()
            .applyEvent(GameCreated.create(gameIdentity)) as GameView
        val withPlayer1 = gameView.applyEvent(PlayerAdded.create(gameIdentity, player1)) as GameView
        val withPlayer2 = withPlayer1.applyEvent(PlayerAdded.create(gameIdentity, player2)) as GameView
        val withPlayer3 = withPlayer2.applyEvent(PlayerAdded.create(gameIdentity, player3)) as GameView

        assertEquals(3, withPlayer3.players.size)
        assertTrue(withPlayer3.players.contains(player1))
        assertTrue(withPlayer3.players.contains(player2))
        assertTrue(withPlayer3.players.contains(player3))
    }

    @Test
    fun `Given GameView with player when same PlayerAdded event applied then player not duplicated (idempotency)`() {
        val gameIdentity = GameIdentity.create()
        val playerIdentity = PlayerIdentity.create()

        val gameView = GameView.empty()
            .applyEvent(GameCreated.create(gameIdentity)) as GameView
        val withPlayer = gameView.applyEvent(PlayerAdded.create(gameIdentity, playerIdentity)) as GameView
        val withPlayerAgain = withPlayer.applyEvent(PlayerAdded.create(gameIdentity, playerIdentity)) as GameView

        assertEquals(1, withPlayerAgain.players.size)
        assertTrue(withPlayerAgain.players.contains(playerIdentity))
    }

    // ============ NEW TESTS FOR CAPACITY CHECKING ============

    @Test
    fun `Given empty GameView when hasCapacity called then returns true`() {
        val gameView = GameView.empty()
            .applyEvent(GameCreated.create(GameIdentity.create())) as GameView

        assertTrue(gameView.hasCapacity())
    }

    @Test
    fun `Given GameView with 3 players when hasCapacity called then returns true`() {
        val gameIdentity = GameIdentity.create()
        val gameView = GameView.empty()
            .applyEvent(GameCreated.create(gameIdentity)) as GameView
        val withPlayer1 = gameView
            .applyEvent(PlayerAdded.create(gameIdentity, PlayerIdentity.create())) as GameView
        val withPlayer2 = withPlayer1
            .applyEvent(PlayerAdded.create(gameIdentity, PlayerIdentity.create())) as GameView
        val with3Players = withPlayer2
            .applyEvent(PlayerAdded.create(gameIdentity, PlayerIdentity.create())) as GameView

        assertEquals(3, with3Players.players.size)
        assertTrue(with3Players.hasCapacity())
        assertFalse(with3Players.isFull())
    }

    @Test
    fun `Given GameView with 4 players when hasCapacity called then returns false`() {
        val gameIdentity = GameIdentity.create()
        val gameView = GameView.empty()
            .applyEvent(GameCreated.create(gameIdentity)) as GameView
        val withPlayer1 = gameView
            .applyEvent(PlayerAdded.create(gameIdentity, PlayerIdentity.create())) as GameView
        val withPlayer2 = withPlayer1
            .applyEvent(PlayerAdded.create(gameIdentity, PlayerIdentity.create())) as GameView
        val withPlayer3 = withPlayer2
            .applyEvent(PlayerAdded.create(gameIdentity, PlayerIdentity.create())) as GameView
        val with4Players = withPlayer3
            .applyEvent(PlayerAdded.create(gameIdentity, PlayerIdentity.create())) as GameView

        assertEquals(4, with4Players.players.size)
        assertFalse(with4Players.hasCapacity())
        assertTrue(with4Players.isFull())
    }

    @Test
    fun `Given GameView in non-DRAFT state when hasCapacity called then returns false`() {
        val gameIdentity = GameIdentity.create()
        val gameView = GameView(
            key = GameViewKey(gameIdentity),
            players = listOf(PlayerIdentity.create()),
            maxPlayers = 4,
            state = GameState.PLAYING,
            lastProcessedEvent = ConcurrentHashMap(),
            lastUpdated = Instant.now()
        )

        assertEquals(1, gameView.players.size)
        assertFalse(gameView.hasCapacity()) // Because state is not DRAFT
        assertFalse(gameView.isFull())
    }

    @Test
    fun `Given empty GameView when isFull called then returns false`() {
        val gameView = GameView.empty()
            .applyEvent(GameCreated.create(GameIdentity.create())) as GameView

        assertFalse(gameView.isFull())
    }
}