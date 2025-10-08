package com.abaddon83.burraco.player.adapter.queryController

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.card.Card
import com.abaddon83.burraco.common.models.card.Suit
import com.abaddon83.burraco.common.models.card.Rank
import com.abaddon83.burraco.player.projection.playerview.PlayerView
import com.abaddon83.burraco.player.projection.playerview.PlayerViewKey
import com.abaddon83.burraco.player.projection.playerview.PlayerViewRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

internal class QueryControllerAdapterTest {

    private lateinit var queryControllerAdapter: QueryControllerAdapter
    private lateinit var playerViewRepository: PlayerViewRepository

    @BeforeEach
    fun setUp() {
        playerViewRepository = PlayerViewRepository()
        queryControllerAdapter = QueryControllerAdapter(playerViewRepository)
    }

    @Test
    fun `given existing player view when getPlayerView then returns Success with PlayerView`() = runBlocking {
        // Given
        val playerIdentity = PlayerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val key = PlayerViewKey(playerIdentity, gameIdentity)
        val cards = listOf(
            Card(Suit.Pike, Rank.Ace),
            Card(Suit.Heart, Rank.King)
        )
        val playerView = PlayerView(
            key = key,
            cards = cards,
            sequences = emptyList(),
            ranks = emptyList(),
            lastProcessedEvent = ConcurrentHashMap(),
            lastUpdated = Instant.now()
        )

        playerViewRepository.save(playerView)

        // When
        val result = queryControllerAdapter.getPlayerView(playerIdentity, gameIdentity)

        // Then
        assertTrue(result.isSuccess)
        val retrievedView = result.getOrNull()
        assertNotNull(retrievedView)
        assertEquals(playerIdentity, retrievedView!!.key.playerIdentity)
        assertEquals(gameIdentity, retrievedView.key.gameIdentity)
        assertEquals(2, retrievedView.cards.size)
        assertEquals(cards, retrievedView.cards)
    }

    @Test
    fun `given non-existing player view when getPlayerView then returns Success with empty PlayerView`() = runBlocking {
        // Given
        val playerIdentity = PlayerIdentity.create()
        val gameIdentity = GameIdentity.create()

        // When
        val result = queryControllerAdapter.getPlayerView(playerIdentity, gameIdentity)

        // Then
        assertTrue(result.isSuccess)
        val retrievedView = result.getOrNull()
        assertNotNull(retrievedView)
        assertEquals(playerIdentity, retrievedView!!.key.playerIdentity)
        assertEquals(gameIdentity, retrievedView.key.gameIdentity)
        assertEquals(0, retrievedView.cards.size)
        assertEquals(0, retrievedView.sequences.size)
        assertEquals(0, retrievedView.ranks.size)
    }

    @Test
    fun `given multiple player views when getPlayerView then returns correct PlayerView for given keys`() = runBlocking {
        // Given
        val player1 = PlayerIdentity.create()
        val player2 = PlayerIdentity.create()
        val game1 = GameIdentity.create()

        val key1 = PlayerViewKey(player1, game1)
        val key2 = PlayerViewKey(player2, game1)

        val cards1 = listOf(Card(Suit.Pike, Rank.Ace))
        val cards2 = listOf(Card(Suit.Heart, Rank.King), Card(Suit.Tile, Rank.Queen))

        val playerView1 = PlayerView(
            key = key1,
            cards = cards1,
            sequences = emptyList(),
            ranks = emptyList(),
            lastProcessedEvent = ConcurrentHashMap(),
            lastUpdated = Instant.now()
        )

        val playerView2 = PlayerView(
            key = key2,
            cards = cards2,
            sequences = emptyList(),
            ranks = emptyList(),
            lastProcessedEvent = ConcurrentHashMap(),
            lastUpdated = Instant.now()
        )

        playerViewRepository.save(playerView1)
        playerViewRepository.save(playerView2)

        // When
        val result1 = queryControllerAdapter.getPlayerView(player1, game1)
        val result2 = queryControllerAdapter.getPlayerView(player2, game1)

        // Then
        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)

        val view1 = result1.getOrNull()!!
        val view2 = result2.getOrNull()!!

        assertEquals(1, view1.cards.size)
        assertEquals(cards1, view1.cards)

        assertEquals(2, view2.cards.size)
        assertEquals(cards2, view2.cards)
    }

    @Test
    fun `given player view with sequences when getPlayerView then returns PlayerView with sequences`() = runBlocking {
        // Given
        val playerIdentity = PlayerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val key = PlayerViewKey(playerIdentity, gameIdentity)

        val cards = listOf(Card(Suit.Pike, Rank.Ace))
        val sequences = listOf(
            listOf(
                Card(Suit.Heart, Rank.Three),
                Card(Suit.Heart, Rank.Four),
                Card(Suit.Heart, Rank.Five)
            )
        )

        val playerView = PlayerView(
            key = key,
            cards = cards,
            sequences = sequences,
            ranks = emptyList(),
            lastProcessedEvent = ConcurrentHashMap(),
            lastUpdated = Instant.now()
        )

        playerViewRepository.save(playerView)

        // When
        val result = queryControllerAdapter.getPlayerView(playerIdentity, gameIdentity)

        // Then
        assertTrue(result.isSuccess)
        val retrievedView = result.getOrNull()!!
        assertEquals(1, retrievedView.sequences.size)
        assertEquals(3, retrievedView.sequences[0].size)
        assertEquals(sequences, retrievedView.sequences)
    }

    @Test
    fun `given player view with ranks when getPlayerView then returns PlayerView with ranks`() = runBlocking {
        // Given
        val playerIdentity = PlayerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val key = PlayerViewKey(playerIdentity, gameIdentity)

        val cards = listOf(Card(Suit.Pike, Rank.Ace))
        val ranks = listOf(
            listOf(
                Card(Suit.Heart, Rank.Seven),
                Card(Suit.Tile, Rank.Seven),
                Card(Suit.Clover, Rank.Seven)
            )
        )

        val playerView = PlayerView(
            key = key,
            cards = cards,
            sequences = emptyList(),
            ranks = ranks,
            lastProcessedEvent = ConcurrentHashMap(),
            lastUpdated = Instant.now()
        )

        playerViewRepository.save(playerView)

        // When
        val result = queryControllerAdapter.getPlayerView(playerIdentity, gameIdentity)

        // Then
        assertTrue(result.isSuccess)
        val retrievedView = result.getOrNull()!!
        assertEquals(1, retrievedView.ranks.size)
        assertEquals(3, retrievedView.ranks[0].size)
        assertEquals(ranks, retrievedView.ranks)
    }
}
