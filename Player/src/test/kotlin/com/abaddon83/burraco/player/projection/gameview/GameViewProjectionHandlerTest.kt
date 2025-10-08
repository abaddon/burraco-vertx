package com.abaddon83.burraco.player.projection.gameview

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.event.game.GameCreated
import com.abaddon83.burraco.player.projection.GameState
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GameViewProjectionHandlerTest {

    private lateinit var repository: GameViewRepository
    private lateinit var projectionHandler: GameViewProjectionHandler
    private lateinit var projectionKey: GameViewKey

    @BeforeEach
    fun setUp() {
        repository = GameViewRepository()
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
}