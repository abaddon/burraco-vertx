package com.abaddon83.burraco.player.projection.playerview

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.card.Card
import com.abaddon83.burraco.common.models.card.Rank
import com.abaddon83.burraco.common.models.card.Suit
import com.abaddon83.burraco.common.models.event.player.PlayerCollectedCard
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class PlayerViewTest {

    @Test
    fun `given empty PlayerView when PlayerCollectedCard applied then new PlayerView with card`() {
        // Given
        val emptyView = PlayerView.empty()
        val playerIdentity = PlayerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val card = Card(Suit.Heart, Rank.Ace)
        val event = PlayerCollectedCard.create(playerIdentity, gameIdentity, card)

        // When
        val result = emptyView.applyEvent(event) as PlayerView

        // Then
        assertEquals(playerIdentity, result.key.playerIdentity)
        assertEquals(gameIdentity, result.key.gameIdentity)
        assertEquals(1, result.cards.size)
        assertEquals(card, result.cards.first())
        assertTrue(result.sequences.isEmpty())
        assertTrue(result.ranks.isEmpty())
    }

    @Test
    fun `given PlayerView with cards when PlayerCollectedCard applied then card added`() {
        // Given
        val playerIdentity = PlayerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val existingCard = Card(Suit.Pike, Rank.King)
        val view = PlayerView(
            PlayerViewKey(playerIdentity, gameIdentity),
            listOf(existingCard),
            emptyList(),
            emptyList(),
            false,
            null,
            java.util.concurrent.ConcurrentHashMap(),
            java.time.Instant.now()
        )
        val newCard = Card(Suit.Heart, Rank.Ace)
        val event = PlayerCollectedCard.create(playerIdentity, gameIdentity, newCard)

        // When
        val result = view.applyEvent(event) as PlayerView

        // Then
        assertEquals(2, result.cards.size)
        assertEquals(existingCard, result.cards[0])
        assertEquals(newCard, result.cards[1])
    }

    @Test
    fun `given PlayerView when PlayerCollectedCard with wrong playerId then throws exception`() {
        // Given
        val playerIdentity = PlayerIdentity.create()
        val differentPlayerIdentity = PlayerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val card = Card(Suit.Heart, Rank.Ace)
        val view = PlayerView(
            PlayerViewKey(playerIdentity, gameIdentity),
            emptyList(),
            emptyList(),
            emptyList(),
            false,
            null,
            java.util.concurrent.ConcurrentHashMap(),
            java.time.Instant.now()
        )
        val event = PlayerCollectedCard.create(differentPlayerIdentity, gameIdentity, card)

        // When & Then
        val exception = assertThrows<IllegalStateException> {
            view.applyEvent(event)
        }
        assertTrue(exception.message!!.contains("Player ID mismatch"))
    }

    @Test
    fun `given PlayerView when PlayerCollectedCard with wrong gameId then throws exception`() {
        // Given
        val playerIdentity = PlayerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val differentGameIdentity = GameIdentity.create()
        val card = Card(Suit.Heart, Rank.Ace)
        val view = PlayerView(
            PlayerViewKey(playerIdentity, gameIdentity),
            emptyList(),
            emptyList(),
            emptyList(),
            false,
            null,
            java.util.concurrent.ConcurrentHashMap(),
            java.time.Instant.now()
        )
        val event = PlayerCollectedCard.create(playerIdentity, differentGameIdentity, card)

        // When & Then
        val exception = assertThrows<IllegalStateException> {
            view.applyEvent(event)
        }
        assertTrue(exception.message!!.contains("Game ID mismatch"))
    }

    @Test
    fun `given empty factory when called then returns correct empty PlayerView`() {
        // When
        val emptyView = PlayerView.empty()

        // Then
        assertEquals(PlayerIdentity.empty(), emptyView.key.playerIdentity)
        assertEquals(GameIdentity.empty(), emptyView.key.gameIdentity)
        assertTrue(emptyView.cards.isEmpty())
        assertTrue(emptyView.sequences.isEmpty())
        assertTrue(emptyView.ranks.isEmpty())
    }
}
