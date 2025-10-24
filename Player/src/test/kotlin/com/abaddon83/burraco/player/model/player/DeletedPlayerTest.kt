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
        val playerNotInGame = PlayerNotInGame(playerIdentity, version, gameIdentity, user)

        // Then
        assertEquals(playerIdentity, playerNotInGame.id)
        assertEquals(version, playerNotInGame.version)
        assertEquals(gameIdentity, playerNotInGame.gameIdentity)
        assertEquals(user, playerNotInGame.user)
    }

    @Test
    fun `given DeletedPlayer when created then has no uncommitted events initially`() {
        // Given
        val playerIdentity = PlayerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val user = "testUser"
        val version = 2L

        // When
        val playerNotInGame = PlayerNotInGame(playerIdentity, version, gameIdentity, user)

        // Then
        assertTrue(playerNotInGame.uncommittedEvents.isEmpty())
    }

    @Test
    fun `given DeletedPlayer with empty user when accessing user then returns empty string`() {
        // Given
        val playerIdentity = PlayerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val user = ""
        val version = 2L

        // When
        val playerNotInGame = PlayerNotInGame(playerIdentity, version, gameIdentity, user)

        // Then
        assertEquals("", playerNotInGame.user)
    }

    @Test
    fun `given two DeletedPlayers with same properties when accessing properties then both have same values`() {
        // Given
        val playerIdentity = PlayerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val user = "testUser"
        val version = 2L

        // When
        val playerNotInGame1 = PlayerNotInGame(playerIdentity, version, gameIdentity, user)
        val playerNotInGame2 = PlayerNotInGame(playerIdentity, version, gameIdentity, user)

        // Then
        assertEquals(playerNotInGame1.id, playerNotInGame2.id)
        assertEquals(playerNotInGame1.version, playerNotInGame2.version)
        assertEquals(playerNotInGame1.gameIdentity, playerNotInGame2.gameIdentity)
        assertEquals(playerNotInGame1.user, playerNotInGame2.user)
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
        val playerNotInGame1 = PlayerNotInGame(playerIdentity1, version, gameIdentity, user)
        val playerNotInGame2 = PlayerNotInGame(playerIdentity2, version, gameIdentity, user)

        // Then
        assertNotEquals(playerNotInGame1.id, playerNotInGame2.id)
        assertEquals(playerNotInGame1.gameIdentity, playerNotInGame2.gameIdentity)
        assertEquals(playerNotInGame1.user, playerNotInGame2.user)
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
        val playerNotInGame = PlayerNotInGame(playerIdentity, newVersion, gameIdentity, user)

        // Then
        assertEquals(playerIdentity, playerNotInGame.id)
        assertEquals(newVersion, playerNotInGame.version)
        assertEquals(gameIdentity, playerNotInGame.gameIdentity)
        assertEquals(user, playerNotInGame.user)
    }
}