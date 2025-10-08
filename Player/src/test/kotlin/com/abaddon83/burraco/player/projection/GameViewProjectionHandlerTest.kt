package com.abaddon83.burraco.player.projection

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.event.game.GameCreated
import com.abaddon83.burraco.common.models.event.game.GameEvent
import com.abaddon83.burraco.player.projection.gameview.GameView
import com.abaddon83.burraco.player.projection.gameview.GameViewKey
import com.abaddon83.burraco.player.projection.gameview.GameViewProjectionHandler
import com.abaddon83.burraco.player.projection.gameview.GameViewRepository
import io.github.abaddon.kcqrs.core.domain.messages.events.EventHeader
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

internal class GameViewProjectionHandlerTest {

    private lateinit var repository: GameViewRepository
    private lateinit var projectionHandler: GameViewProjectionHandler
    private lateinit var projectionKey: GameViewKey

    @BeforeEach
    fun setUp() {
        repository = GameViewRepository()
        projectionKey = GameViewKey(GameIdentity.empty())
        projectionHandler = GameViewProjectionHandler(
            repository = repository,
            projectionKey = projectionKey
        )
    }

    @Test
    fun `Given empty projection when GameCreated event applied then new projection created with correct state`() = runBlocking {
        val gameIdentity = GameIdentity.create()
        val gameCreatedEvent = GameCreated.create(gameIdentity)

        val emptyProjection = repository.emptyProjection(projectionKey)
        val result = projectionHandler.applyEventToProjection(emptyProjection, gameCreatedEvent)

        assertEquals(GameViewKey(gameIdentity), result.key)
        assertTrue(result.players.isEmpty())
        assertEquals(GameState.DRAFT, result.state)
        assertNotNull(result.lastUpdated)
    }

    @Test
    fun `Given non-empty projection when GameCreated event applied then event skipped with error log`() = runBlocking {
        val gameIdentity1 = GameIdentity.create()
        val gameIdentity2 = GameIdentity.create()
        val gameCreatedEvent = GameCreated.create(gameIdentity2)
        
        // Create a projection that already has a non-empty key
        val existingProjection = GameView(
            key = GameViewKey(gameIdentity1),
            players = emptyList(),
            state = GameState.DRAFT,
            lastProcessedEvent = mutableMapOf(),
            lastUpdated = null
        )

        val result = projectionHandler.applyEventToProjection(existingProjection, gameCreatedEvent)

        // Should return the original projection unchanged
        assertEquals(existingProjection, result)
        assertEquals(GameViewKey(gameIdentity1), result.key)
    }

    @Test
    fun `Given unsupported event when applied then projection returned unchanged`() = runBlocking {
        val gameIdentity = GameIdentity.create()
        
        // Create a mock unsupported event
        val unsupportedEvent = object : GameEvent() {
            override val messageId = UUID.randomUUID()
            override val header = EventHeader.create("Test")
            override val aggregateId = gameIdentity
        }

        val emptyProjection = repository.emptyProjection(projectionKey)
        val result = projectionHandler.applyEventToProjection(emptyProjection, unsupportedEvent)

        // Should return the original projection unchanged
        assertEquals(emptyProjection, result)
    }

    @Test
    fun `Given GameCreated event when error occurs then handleEventError called and projection unchanged`() = runBlocking {
        val gameIdentity = GameIdentity.create()
        val gameCreatedEvent = GameCreated.create(gameIdentity)
        val originalProjection = repository.emptyProjection(projectionKey)
        val testException = RuntimeException("Test error")

        val result = projectionHandler.handleEventError(originalProjection, gameCreatedEvent, testException)

        // Should return the original projection unchanged when error occurs
        assertEquals(originalProjection, result)
    }

    @Test
    fun `Given valid GameCreated event when processed through onEvent then projection saved correctly`() = runBlocking {
        val gameIdentity = GameIdentity.create()
        val gameCreatedEvent = GameCreated.create(gameIdentity)

        val result = projectionHandler.onEvent(gameCreatedEvent)

        assertTrue(result.isSuccess)
        
        // Verify the projection was saved to repository
        val savedProjection = repository.getByKey(projectionKey).getOrThrow()
        assertEquals(GameViewKey(gameIdentity), savedProjection.key)
        assertEquals(GameState.DRAFT, savedProjection.state)
        assertTrue(savedProjection.players.isEmpty())
    }
}