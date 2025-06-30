package com.abaddon83.burraco.player.model.player

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.player.event.PlayerCreated
import com.abaddon83.burraco.player.event.PlayerDeleted
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class PlayerDraftTest {

    @Test
    fun `given empty PlayerDraft when createPlayer then returns new PlayerDraft with PlayerCreated event`() {
        // Given
        val emptyPlayerDraft = PlayerDraft.empty()
        val playerIdentity = PlayerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val user = "testUser"

        // When
        val result = emptyPlayerDraft.createPlayer(playerIdentity, user, gameIdentity)

        // Then
        assertEquals(playerIdentity, result.id)
        assertEquals(gameIdentity, result.gameIdentity)
        assertEquals(user, result.user)
        assertEquals(1, result.uncommittedEvents.size)
        
        val event = result.uncommittedEvents.first() as PlayerCreated
        assertEquals(playerIdentity, event.aggregateId)
        assertEquals(gameIdentity, event.gameIdentity)
        assertEquals(user, event.user)
    }

    @Test
    fun `given empty PlayerDraft when createPlayer with empty user then returns new PlayerDraft with empty user`() {
        // Given
        val emptyPlayerDraft = PlayerDraft.empty()
        val playerIdentity = PlayerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val user = ""

        // When
        val result = emptyPlayerDraft.createPlayer(playerIdentity, user, gameIdentity)

        // Then
        assertEquals("", result.user)
        val event = result.uncommittedEvents.first() as PlayerCreated
        assertEquals("", event.user)
    }

    @Test
    fun `given existing PlayerDraft when createPlayer then throws IllegalStateException`() {
        // Given
        val playerIdentity = PlayerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val user = "testUser"
        val existingPlayerDraft = PlayerDraft(playerIdentity, 1, gameIdentity, user)

        // When & Then
        val exception = assertThrows<IllegalStateException> {
            existingPlayerDraft.createPlayer(PlayerIdentity.create(), "anotherUser", gameIdentity)
        }
        assertTrue(exception.message!!.contains("already created"))
    }

    @Test
    fun `given existing PlayerDraft when deletePlayer then returns DeletedPlayer with PlayerDeleted event`() {
        // Given
        val playerIdentity = PlayerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val user = "testUser"
        val existingPlayerDraft = PlayerDraft(playerIdentity, 1, gameIdentity, user)

        // When
        val result = existingPlayerDraft.deletePlayer()

        // Then
        assertTrue(result is DeletedPlayer)
        assertEquals(playerIdentity, result.id)
        assertEquals(gameIdentity, result.gameIdentity)
        assertEquals(user, result.user)
        assertEquals(1, result.uncommittedEvents.size)

        val event = result.uncommittedEvents.first() as PlayerDeleted
        assertEquals(playerIdentity, event.aggregateId)
    }

    @Test
    fun `given empty PlayerDraft when deletePlayer then throws IllegalStateException`() {
        // Given
        val emptyPlayerDraft = PlayerDraft.empty()

        // When & Then
        val exception = assertThrows<IllegalStateException> {
            emptyPlayerDraft.deletePlayer()
        }
        assertTrue(exception.message!!.contains("does not exist"))
    }

    @Test
    fun `given PlayerDraft created with specific values when accessing properties then returns correct values`() {
        // Given
        val playerIdentity = PlayerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val user = "testUser"
        val version = 5L

        // When
        val playerDraft = PlayerDraft(playerIdentity, version, gameIdentity, user)

        // Then
        assertEquals(playerIdentity, playerDraft.id)
        assertEquals(version, playerDraft.version)
        assertEquals(gameIdentity, playerDraft.gameIdentity)
        assertEquals(user, playerDraft.user)
    }

    @Test
    fun `given empty PlayerDraft factory when create then returns PlayerDraft with empty values`() {
        // When
        val emptyPlayerDraft = PlayerDraft.empty()

        // Then
        assertEquals(PlayerIdentity.empty(), emptyPlayerDraft.id)
        assertEquals(0L, emptyPlayerDraft.version)
        assertEquals(GameIdentity.empty(), emptyPlayerDraft.gameIdentity)
        assertEquals("", emptyPlayerDraft.user)
    }

    @Test
    fun `given PlayerDraft when createPlayer multiple times with same identity then throws IllegalStateException`() {
        // Given
        val emptyPlayerDraft = PlayerDraft.empty()
        val playerIdentity = PlayerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val user = "testUser"

        // First creation
        val firstResult = emptyPlayerDraft.createPlayer(playerIdentity, user, gameIdentity)

        // When & Then - Try to create again with same identity on the result
        val exception = assertThrows<IllegalStateException> {
            firstResult.createPlayer(playerIdentity, user, gameIdentity)
        }
        assertTrue(exception.message!!.contains("already created"))
    }
}