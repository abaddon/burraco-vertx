package com.abaddon83.burraco.player.port

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.event.player.PlayerCreated
import com.abaddon83.burraco.common.models.event.player.PlayerEvent
import com.abaddon83.burraco.player.model.player.PlayerDraft
import com.abaddon83.burraco.player.model.player.Player
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ExternalEventPublisherPortTest {

    private class TestExternalEventPublisherPort : ExternalEventPublisherPort {
        val publishedEvents = mutableListOf<Pair<Player, PlayerEvent>>()
        var shouldFail = false

        override suspend fun publish(aggregate: Player, event: PlayerEvent): Result<Unit> {
            return if (shouldFail) {
                Result.failure(RuntimeException("Test failure"))
            } else {
                publishedEvents.add(Pair(aggregate, event))
                Result.success(Unit)
            }
        }
    }

    @Test
    fun `should successfully publish events when implementation succeeds`() {
        val port = TestExternalEventPublisherPort()
        val gameIdentity = GameIdentity.create()
        val playerIdentity = PlayerIdentity.create()
        val aggregate = PlayerDraft.empty().createPlayer(playerIdentity, "testUser", gameIdentity)
        val event = PlayerCreated.create(playerIdentity, gameIdentity, "testUser")

        runBlocking {
            val result = port.publish(aggregate, event)
            
            assertTrue(result.isSuccess)
            assertEquals(1, port.publishedEvents.size)
            assertEquals(aggregate, port.publishedEvents[0].first)
            assertEquals(event, port.publishedEvents[0].second)
        }
    }

    @Test
    fun `should return failure when implementation fails`() {
        val port = TestExternalEventPublisherPort()
        port.shouldFail = true
        
        val gameIdentity = GameIdentity.create()
        val playerIdentity = PlayerIdentity.create()
        val aggregate = PlayerDraft.empty().createPlayer(playerIdentity, "testUser", gameIdentity)
        val event = PlayerCreated.create(playerIdentity, gameIdentity, "testUser")

        runBlocking {
            val result = port.publish(aggregate, event)
            
            assertTrue(result.isFailure)
            assertEquals("Test failure", result.exceptionOrNull()?.message)
            assertEquals(0, port.publishedEvents.size)
        }
    }

    @Test
    fun `should handle multiple publish calls correctly`() {
        val port = TestExternalEventPublisherPort()
        val gameIdentity = GameIdentity.create()
        val playerIdentity1 = PlayerIdentity.create()
        val playerIdentity2 = PlayerIdentity.create()
        
        val aggregate1 = PlayerDraft.empty().createPlayer(playerIdentity1, "user1", gameIdentity)
        val event1 = PlayerCreated.create(playerIdentity1, gameIdentity, "user1")
        
        val aggregate2 = PlayerDraft.empty().createPlayer(playerIdentity2, "user2", gameIdentity)
        val event2 = PlayerCreated.create(playerIdentity2, gameIdentity, "user2")

        runBlocking {
            val result1 = port.publish(aggregate1, event1)
            val result2 = port.publish(aggregate2, event2)
            
            assertTrue(result1.isSuccess)
            assertTrue(result2.isSuccess)
            assertEquals(2, port.publishedEvents.size)
            
            assertEquals(aggregate1, port.publishedEvents[0].first)
            assertEquals(event1, port.publishedEvents[0].second)
            
            assertEquals(aggregate2, port.publishedEvents[1].first)
            assertEquals(event2, port.publishedEvents[1].second)
        }
    }
}