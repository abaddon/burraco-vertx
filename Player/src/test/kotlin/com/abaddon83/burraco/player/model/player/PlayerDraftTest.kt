package com.abaddon83.burraco.player.model.player

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.card.Card
import com.abaddon83.burraco.common.models.card.Rank
import com.abaddon83.burraco.common.models.card.Suit
import com.abaddon83.burraco.common.models.event.player.PlayerCreated
import com.abaddon83.burraco.common.models.event.player.PlayerDeleted
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
        assertTrue(result is com.abaddon83.burraco.player.model.player.PlayerNotInGame)
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

    @Test
    fun `given PlayerDraft when addCard with matching playerId and gameId then returns PlayerDraft with card added`() {
        // Given
        val playerIdentity = PlayerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val user = "testUser"
        val playerDraft = PlayerDraft(playerIdentity, 1, gameIdentity, user, emptyList())
        val card = Card(Suit.Heart, Rank.Ace)

        // When
        val result = playerDraft.addCard(playerIdentity, gameIdentity, card)

        // Then
        assertEquals(playerIdentity, result.id)
        assertEquals(gameIdentity, result.gameIdentity)
        assertEquals(1, result.cards.size)
        assertEquals(card, result.cards.first())
        // Note: addCard does not raise events - it's triggered by external Dealer events
        assertEquals(1, result.uncommittedEvents.size)
    }

    @Test
    fun `given PlayerDraft with existing cards when addCard then appends card to list`() {
        // Given
        val playerIdentity = PlayerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val user = "testUser"
        val existingCard = Card(Suit.Pike, Rank.King)
        val playerDraft = PlayerDraft(playerIdentity, 1, gameIdentity, user, listOf(existingCard))
        val newCard = Card(Suit.Heart, Rank.Ace)

        // When
        val result = playerDraft.addCard(playerIdentity, gameIdentity, newCard)

        // Then
        assertEquals(2, result.cards.size)
        assertEquals(existingCard, result.cards[0])
        assertEquals(newCard, result.cards[1])
    }

    @Test
    fun `given PlayerDraft when addCard with wrong playerId then throws IllegalStateException`() {
        // Given
        val playerIdentity = PlayerIdentity.create()
        val differentPlayerIdentity = PlayerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val user = "testUser"
        val playerDraft = PlayerDraft(playerIdentity, 1, gameIdentity, user, emptyList())
        val card = Card(Suit.Heart, Rank.Ace)

        // When & Then
        val exception = assertThrows<IllegalStateException> {
            playerDraft.addCard(differentPlayerIdentity, gameIdentity, card)
        }
        assertTrue(exception.message!!.contains("Card dealt to player"))
        assertTrue(exception.message!!.contains("but current player is"))
    }

    @Test
    fun `given PlayerDraft when addCard with wrong gameId then throws IllegalStateException`() {
        // Given
        val playerIdentity = PlayerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val differentGameIdentity = GameIdentity.create()
        val user = "testUser"
        val playerDraft = PlayerDraft(playerIdentity, 1, gameIdentity, user, emptyList())
        val card = Card(Suit.Heart, Rank.Ace)

        // When & Then
        val exception = assertThrows<IllegalStateException> {
            playerDraft.addCard(playerIdentity, differentGameIdentity, card)
        }
        assertTrue(exception.message!!.contains("Card dealt for game"))
        assertTrue(exception.message!!.contains("but current game is"))
    }

    @Test
    fun `given PlayerDraft when adding multiple cards then all cards are stored in order`() {
        // Given
        val playerIdentity = PlayerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val user = "testUser"
        val playerDraft = PlayerDraft(playerIdentity, 1, gameIdentity, user, emptyList())

        val card1 = Card(Suit.Heart, Rank.Ace)
        val card2 = Card(Suit.Pike, Rank.King)
        val card3 = Card(Suit.Tile, Rank.Queen)

        // When
        val result1 = playerDraft.addCard(playerIdentity, gameIdentity, card1)
        val result2 = result1.addCard(playerIdentity, gameIdentity, card2)
        val result3 = result2.addCard(playerIdentity, gameIdentity, card3)

        // Then
        assertEquals(3, result3.cards.size)
        assertEquals(card1, result3.cards[0])
        assertEquals(card2, result3.cards[1])
        assertEquals(card3, result3.cards[2])
    }

    @Test
    fun `given empty PlayerDraft when created with factory then has empty cards list`() {
        // When
        val emptyPlayerDraft = PlayerDraft.empty()

        // Then
        assertTrue(emptyPlayerDraft.cards.isEmpty())
    }
}