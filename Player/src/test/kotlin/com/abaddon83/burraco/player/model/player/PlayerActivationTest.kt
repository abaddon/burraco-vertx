package com.abaddon83.burraco.player.model.player

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.card.Card
import com.abaddon83.burraco.common.models.card.Rank
import com.abaddon83.burraco.common.models.card.Suit
import com.abaddon83.burraco.common.models.event.player.PlayerActivated
import com.abaddon83.burraco.common.models.event.player.PlayerWaitingTurn
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * Unit tests for player activation feature.
 *
 * Tests cover:
 * - PlayerDraft → PlayerActive transition (activatePlayer)
 * - PlayerDraft → PlayerWaiting transition (setWaiting)
 * - Event generation and state validation
 * - Error cases (empty player, wrong state)
 */
internal class PlayerActivationTest {

    @Test
    fun `given PlayerDraft when activatePlayer then returns PlayerActive with PlayerActivated event`() {
        // Given
        val playerIdentity = PlayerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val teamMateId = PlayerIdentity.create()
        val user = "testUser"
        val card = Card(Suit.Heart, Rank.Ace)
        val playerDraft = PlayerDraft(playerIdentity, 1, gameIdentity, user, listOf(card))

        // When
        val result = playerDraft.activatePlayer(teamMateId)

        // Then
        assertTrue(result is PlayerActive)
        assertEquals(playerIdentity, result.id)
        assertEquals(gameIdentity, result.gameIdentity)
        assertEquals(user, result.user)
        assertEquals(1, result.cards.size)
        assertEquals(card, result.cards.first())
        assertEquals(teamMateId, result.teamMateId)
        assertEquals(2L, result.version) // Version incremented
        assertEquals(1, result.uncommittedEvents.size)

        val event = result.uncommittedEvents.first() as PlayerActivated
        assertEquals(playerIdentity, event.aggregateId)
        assertEquals(gameIdentity, event.gameIdentity)
        assertEquals(teamMateId, event.teamMateId)
    }

    @Test
    fun `given PlayerDraft when activatePlayer with null teammate then returns PlayerActive with null teamMateId`() {
        // Given
        val playerIdentity = PlayerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val user = "testUser"
        val playerDraft = PlayerDraft(playerIdentity, 1, gameIdentity, user, emptyList())

        // When
        val result = playerDraft.activatePlayer(null)

        // Then
        assertTrue(result is PlayerActive)
        assertNull(result.teamMateId)

        val event = result.uncommittedEvents.first() as PlayerActivated
        assertNull(event.teamMateId)
    }

    @Test
    fun `given empty PlayerDraft when activatePlayer then throws IllegalStateException`() {
        // Given
        val emptyPlayerDraft = PlayerDraft.empty()

        // When & Then
        val exception = assertThrows<IllegalStateException> {
            emptyPlayerDraft.activatePlayer(null)
        }
        assertTrue(exception.message!!.contains("does not exist"))
    }

    @Test
    fun `given PlayerDraft when setWaiting then returns PlayerWaiting with PlayerWaitingTurn event`() {
        // Given
        val playerIdentity = PlayerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val teamMateId = PlayerIdentity.create()
        val user = "testUser"
        val card = Card(Suit.Pike, Rank.King)
        val playerDraft = PlayerDraft(playerIdentity, 1, gameIdentity, user, listOf(card))

        // When
        val result = playerDraft.setWaiting(teamMateId)

        // Then
        assertTrue(result is PlayerWaiting)
        assertEquals(playerIdentity, result.id)
        assertEquals(gameIdentity, result.gameIdentity)
        assertEquals(user, result.user)
        assertEquals(1, result.cards.size)
        assertEquals(card, result.cards.first())
        assertEquals(teamMateId, result.teamMateId)
        assertEquals(2L, result.version) // Version incremented
        assertEquals(1, result.uncommittedEvents.size)

        val event = result.uncommittedEvents.first() as PlayerWaitingTurn
        assertEquals(playerIdentity, event.aggregateId)
        assertEquals(gameIdentity, event.gameIdentity)
        assertEquals(teamMateId, event.teamMateId)
    }

    @Test
    fun `given PlayerDraft when setWaiting with null teammate then returns PlayerWaiting with null teamMateId`() {
        // Given
        val playerIdentity = PlayerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val user = "testUser"
        val playerDraft = PlayerDraft(playerIdentity, 1, gameIdentity, user, emptyList())

        // When
        val result = playerDraft.setWaiting(null)

        // Then
        assertTrue(result is PlayerWaiting)
        assertNull(result.teamMateId)

        val event = result.uncommittedEvents.first() as PlayerWaitingTurn
        assertNull(event.teamMateId)
    }

    @Test
    fun `given empty PlayerDraft when setWaiting then throws IllegalStateException`() {
        // Given
        val emptyPlayerDraft = PlayerDraft.empty()

        // When & Then
        val exception = assertThrows<IllegalStateException> {
            emptyPlayerDraft.setWaiting(null)
        }
        assertTrue(exception.message!!.contains("does not exist"))
    }

    @Test
    fun `given PlayerDraft with multiple cards when activatePlayer then PlayerActive preserves all cards`() {
        // Given
        val playerIdentity = PlayerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val user = "testUser"
        val cards = listOf(
            Card(Suit.Heart, Rank.Ace),
            Card(Suit.Pike, Rank.King),
            Card(Suit.Tile, Rank.Queen)
        )
        val playerDraft = PlayerDraft(playerIdentity, 1, gameIdentity, user, cards)

        // When
        val result = playerDraft.activatePlayer(null)

        // Then
        assertEquals(3, result.cards.size)
        assertEquals(cards, result.cards)
    }

    @Test
    fun `given PlayerDraft with multiple cards when setWaiting then PlayerWaiting preserves all cards`() {
        // Given
        val playerIdentity = PlayerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val user = "testUser"
        val cards = listOf(
            Card(Suit.Heart, Rank.Ace),
            Card(Suit.Pike, Rank.King),
            Card(Suit.Tile, Rank.Queen)
        )
        val playerDraft = PlayerDraft(playerIdentity, 1, gameIdentity, user, cards)

        // When
        val result = playerDraft.setWaiting(null)

        // Then
        assertEquals(3, result.cards.size)
        assertEquals(cards, result.cards)
    }

    @Test
    fun `given PlayerActive factory when empty then returns PlayerActive with empty values`() {
        // When
        val emptyPlayerActive = PlayerActive.empty()

        // Then
        assertEquals(PlayerIdentity.empty(), emptyPlayerActive.id)
        assertEquals(0L, emptyPlayerActive.version)
        assertEquals(GameIdentity.empty(), emptyPlayerActive.gameIdentity)
        assertEquals("", emptyPlayerActive.user)
        assertTrue(emptyPlayerActive.cards.isEmpty())
        assertNull(emptyPlayerActive.teamMateId)
    }

    @Test
    fun `given PlayerWaiting factory when empty then returns PlayerWaiting with empty values`() {
        // When
        val emptyPlayerWaiting = PlayerWaiting.empty()

        // Then
        assertEquals(PlayerIdentity.empty(), emptyPlayerWaiting.id)
        assertEquals(0L, emptyPlayerWaiting.version)
        assertEquals(GameIdentity.empty(), emptyPlayerWaiting.gameIdentity)
        assertEquals("", emptyPlayerWaiting.user)
        assertTrue(emptyPlayerWaiting.cards.isEmpty())
        assertNull(emptyPlayerWaiting.teamMateId)
    }
}
