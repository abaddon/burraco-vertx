package com.abaddon83.burraco.player.model.player

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class DeletedPlayerTest {

    @Test
    fun `given DeletedPlayer created with specific values when accessing properties then returns correct values`() {
        // Given
        val playerIdentity = PlayerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val user = "testUser"
        val version = 2L

        // When
        val deletedPlayer = DeletedPlayer(playerIdentity, version, gameIdentity, user)

        // Then
        assertEquals(playerIdentity, deletedPlayer.id)
        assertEquals(version, deletedPlayer.version)
        assertEquals(gameIdentity, deletedPlayer.gameIdentity)
        assertEquals(user, deletedPlayer.user)
    }

    @Test
    fun `given DeletedPlayer when created then has no uncommitted events initially`() {
        // Given
        val playerIdentity = PlayerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val user = "testUser"
        val version = 2L

        // When
        val deletedPlayer = DeletedPlayer(playerIdentity, version, gameIdentity, user)

        // Then
        assertTrue(deletedPlayer.uncommittedEvents.isEmpty())
    }

    @Test
    fun `given DeletedPlayer with empty user when accessing user then returns empty string`() {
        // Given
        val playerIdentity = PlayerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val user = ""
        val version = 2L

        // When
        val deletedPlayer = DeletedPlayer(playerIdentity, version, gameIdentity, user)

        // Then
        assertEquals("", deletedPlayer.user)
    }

    @Test
    fun `given two DeletedPlayers with same properties when accessing properties then both have same values`() {
        // Given
        val playerIdentity = PlayerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val user = "testUser"
        val version = 2L

        // When
        val deletedPlayer1 = DeletedPlayer(playerIdentity, version, gameIdentity, user)
        val deletedPlayer2 = DeletedPlayer(playerIdentity, version, gameIdentity, user)

        // Then
        assertEquals(deletedPlayer1.id, deletedPlayer2.id)
        assertEquals(deletedPlayer1.version, deletedPlayer2.version)
        assertEquals(deletedPlayer1.gameIdentity, deletedPlayer2.gameIdentity)
        assertEquals(deletedPlayer1.user, deletedPlayer2.user)
    }

    @Test
    fun `given two DeletedPlayers with different identities when accessing identities then they are different`() {
        // Given
        val playerIdentity1 = PlayerIdentity.create()
        val playerIdentity2 = PlayerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val user = "testUser"
        val version = 2L

        // When
        val deletedPlayer1 = DeletedPlayer(playerIdentity1, version, gameIdentity, user)
        val deletedPlayer2 = DeletedPlayer(playerIdentity2, version, gameIdentity, user)

        // Then
        assertNotEquals(deletedPlayer1.id, deletedPlayer2.id)
        assertEquals(deletedPlayer1.gameIdentity, deletedPlayer2.gameIdentity)
        assertEquals(deletedPlayer1.user, deletedPlayer2.user)
    }

    @Test
    fun `given DeletedPlayer created from PlayerDraft transition when properties preserved then transition is valid`() {
        // Given - Simulate what happens when PlayerDraft.deletePlayer() is called
        val playerIdentity = PlayerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val user = "testUser"
        val originalVersion = 1L
        val newVersion = 2L

        // Simulate the transition that happens in PlayerDraft.apply(PlayerDeleted)
        val deletedPlayer = DeletedPlayer(playerIdentity, newVersion, gameIdentity, user)

        // Then
        assertEquals(playerIdentity, deletedPlayer.id)
        assertEquals(newVersion, deletedPlayer.version)
        assertEquals(gameIdentity, deletedPlayer.gameIdentity)
        assertEquals(user, deletedPlayer.user)
    }
}