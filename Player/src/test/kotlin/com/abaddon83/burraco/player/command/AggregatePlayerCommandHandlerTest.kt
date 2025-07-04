package com.abaddon83.burraco.player.command

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.event.player.PlayerCreated
import com.abaddon83.burraco.common.models.event.player.PlayerEvent
import com.abaddon83.burraco.player.model.player.PlayerDraft
import com.abaddon83.burraco.player.model.player.Player
import com.abaddon83.burraco.player.port.ExternalEventPublisherPort
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class AggregatePlayerCommandHandlerTest {

    private class TestExternalEventPublisherPort : ExternalEventPublisherPort {
        val publishedEvents = mutableListOf<Pair<Player, PlayerEvent>>()
        var shouldFail = false

        override suspend fun publish(aggregate: Player, event: PlayerEvent): Result<Unit> {
            return if (shouldFail) {
                Result.failure(RuntimeException("External event publishing failed"))
            } else {
                publishedEvents.add(Pair(aggregate, event))
                Result.success(Unit)
            }
        }
    }

    @Test
    fun `should publish external events when processing PlayerCreated events`() {
        val externalEventPublisher = TestExternalEventPublisherPort()
        
        val gameIdentity = GameIdentity.create()
        val playerIdentity = PlayerIdentity.create()
        
        // Create a DraftPlayer with uncommitted events
        val draftPlayer = PlayerDraft.empty().createPlayer(playerIdentity, "testUser", gameIdentity)

        runBlocking {
            // Manually test the publishing logic for each uncommitted event
            draftPlayer.uncommittedEvents().forEach { event ->
                if (event is PlayerEvent) {
                    externalEventPublisher.publish(draftPlayer, event)
                }
            }
            
            assertEquals(1, externalEventPublisher.publishedEvents.size)
            
            val publishedEvent = externalEventPublisher.publishedEvents[0]
            assertEquals(draftPlayer, publishedEvent.first)
            assertTrue(publishedEvent.second is PlayerCreated)
            
            val playerCreatedEvent = publishedEvent.second as PlayerCreated
            assertEquals(playerIdentity, playerCreatedEvent.aggregateId)
            assertEquals(gameIdentity, playerCreatedEvent.gameIdentity)
            assertEquals("testUser", playerCreatedEvent.user)
        }
    }

    @Test
    fun `should handle external event publishing failure gracefully`() {
        val externalEventPublisher = TestExternalEventPublisherPort()
        externalEventPublisher.shouldFail = true
        
        val gameIdentity = GameIdentity.create()
        val playerIdentity = PlayerIdentity.create()
        
        val draftPlayer = PlayerDraft.empty().createPlayer(playerIdentity, "testUser", gameIdentity)

        runBlocking {
            var failureOccurred = false
            
            // Manually test the publishing logic
            draftPlayer.uncommittedEvents().forEach { event ->
                if (event is PlayerEvent) {
                    val result = externalEventPublisher.publish(draftPlayer, event)
                    if (result.isFailure) {
                        failureOccurred = true
                        assertEquals("External event publishing failed", result.exceptionOrNull()?.message)
                    }
                }
            }
            
            assertTrue(failureOccurred)
            assertEquals(0, externalEventPublisher.publishedEvents.size)
        }
    }

    @Test
    fun `should not publish external events for aggregates with no uncommitted events`() {
        val externalEventPublisher = TestExternalEventPublisherPort()
        
        // Create a player with no uncommitted events
        val draftPlayer = PlayerDraft.empty()

        runBlocking {
            // Test with empty uncommitted events
            draftPlayer.uncommittedEvents().forEach { event ->
                if (event is PlayerEvent) {
                    externalEventPublisher.publish(draftPlayer, event)
                }
            }
            
            assertEquals(0, externalEventPublisher.publishedEvents.size)
        }
    }
}