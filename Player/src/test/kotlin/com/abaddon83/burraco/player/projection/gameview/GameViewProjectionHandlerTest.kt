package com.abaddon83.burraco.player.projection.gameview

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.event.game.GameCreated
import com.abaddon83.burraco.common.models.event.game.PlayerAdded
import com.abaddon83.burraco.player.projection.GameState
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GameViewProjectionHandlerTest {

    private lateinit var repository: InMemoryGameViewRepository
    private lateinit var projectionHandler: GameViewProjectionHandler
    private lateinit var projectionKey: GameViewKey

    @BeforeEach
    fun setUp() {
        repository = InMemoryGameViewRepository()
        projectionKey = GameViewKey(GameIdentity.empty())
        projectionHandler = GameViewProjectionHandler(
            repository = repository
        )
    }

    @Test
    fun `Given GameCreated event when processed through onEvent then projection key extracted correctly`() =
        runBlocking {
            val gameIdentity = GameIdentity.create()
            val gameCreatedEvent = GameCreated.create(gameIdentity)

            val projectionKey = projectionHandler.getProjectionKey(gameCreatedEvent)

            assertEquals(GameViewKey(gameIdentity), projectionKey)
        }

    @Test
    fun `Given valid GameCreated event when processed through onEvent then projection saved correctly`() = runBlocking {
        val gameIdentity = GameIdentity.create()
        val gameCreatedEvent = GameCreated.create(gameIdentity)

        val result = projectionHandler.onEvent(gameCreatedEvent)

        assertTrue(result.isSuccess)

        // Verify the projection was saved to repository with the correct key from the event
        val eventProjectionKey = GameViewKey(gameIdentity)
        val savedProjection = repository.getByKey(eventProjectionKey).getOrThrow()
        assertEquals(GameViewKey(gameIdentity), savedProjection.key)
        assertEquals(GameState.DRAFT, savedProjection.state)
        assertTrue(savedProjection.players.isEmpty())
    }

    // ============ NEW TESTS FOR PLAYERADDED EVENT ============

    @Test
    fun `Given PlayerAdded event when getProjectionKey called then projection key extracted correctly`() = runBlocking {
        val gameIdentity = GameIdentity.create()
        val playerIdentity = PlayerIdentity.create()
        val playerAddedEvent = PlayerAdded.create(gameIdentity, playerIdentity)

        val projectionKey = projectionHandler.getProjectionKey(playerAddedEvent)

        assertEquals(GameViewKey(gameIdentity), projectionKey)
    }

    @Test
    fun `Given valid PlayerAdded event when applied directly then player added to projection`() = runBlocking {
        val gameIdentity = GameIdentity.create()
        val playerIdentity = PlayerIdentity.create()
        val gameCreatedEvent = GameCreated.create(gameIdentity)
        val playerAddedEvent = PlayerAdded.create(gameIdentity, playerIdentity)

        // Create game view by applying events directly
        val gameView = GameView.empty().applyEvent(gameCreatedEvent) as GameView
        val updatedView = gameView.applyEvent(playerAddedEvent) as GameView

        // Verify the player was added
        assertEquals(1, updatedView.players.size)
        assertTrue(updatedView.players.contains(playerIdentity))
    }

    @Test
    fun `Given multiple PlayerAdded events when applied directly then all players added to projection`() = runBlocking {
        val gameIdentity = GameIdentity.create()
        val player1 = PlayerIdentity.create()
        val player2 = PlayerIdentity.create()
        val player3 = PlayerIdentity.create()

        // Create game and add players by applying events directly
        val gameView = GameView.empty().applyEvent(GameCreated.create(gameIdentity)) as GameView
        val withPlayer1 = gameView.applyEvent(PlayerAdded.create(gameIdentity, player1)) as GameView
        val withPlayer2 = withPlayer1.applyEvent(PlayerAdded.create(gameIdentity, player2)) as GameView
        val withPlayer3 = withPlayer2.applyEvent(PlayerAdded.create(gameIdentity, player3)) as GameView

        // Verify all players are in the projection
        assertEquals(3, withPlayer3.players.size)
        assertTrue(withPlayer3.players.contains(player1))
        assertTrue(withPlayer3.players.contains(player2))
        assertTrue(withPlayer3.players.contains(player3))
    }
}