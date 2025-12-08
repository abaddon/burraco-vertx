package com.abaddon83.burraco.player.projection.playerview

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.card.Card
import com.abaddon83.burraco.common.models.card.Rank
import com.abaddon83.burraco.common.models.card.Suit
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

internal class PlayerViewRepositoryTest {

    private lateinit var repository: InMemoryPlayerViewRepository

    @BeforeEach
    fun setup() {
        repository = InMemoryPlayerViewRepository()
    }

    @Test
    fun `given empty repository when getByKey called then return empty projection`() = runBlocking {
        // Given
        val playerIdentity = PlayerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val key = PlayerViewKey(playerIdentity, gameIdentity)

        // When
        val result = repository.getByKey(key)

        // Then
        assertTrue(result.isSuccess)
        val projection = result.getOrThrow()
        assertEquals(playerIdentity, projection.key.playerIdentity)
        assertEquals(gameIdentity, projection.key.gameIdentity)
        assertTrue(projection.cards.isEmpty())
    }

    @Test
    fun `given projection saved when getByKey called then return saved projection`() = runBlocking {
        // Given
        val playerIdentity = PlayerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val key = PlayerViewKey(playerIdentity, gameIdentity)
        val card = Card(Suit.Heart, Rank.Ace)
        val projection = PlayerView(
            key,
            listOf(card),
            emptyList(),
            emptyList(),
            false,
            null,
            ConcurrentHashMap(),
            Instant.now()
        )
        repository.save(projection)

        // When
        val result = repository.getByKey(key)

        // Then
        assertTrue(result.isSuccess)
        val savedProjection = result.getOrThrow()
        assertEquals(playerIdentity, savedProjection.key.playerIdentity)
        assertEquals(gameIdentity, savedProjection.key.gameIdentity)
        assertEquals(1, savedProjection.cards.size)
        assertEquals(card, savedProjection.cards.first())
    }

    @Test
    fun `given multiple projections when save called then all stored correctly`() = runBlocking {
        // Given
        val player1 = PlayerIdentity.create()
        val player2 = PlayerIdentity.create()
        val game = GameIdentity.create()
        val projection1 = PlayerView(
            PlayerViewKey(player1, game),
            listOf(Card(Suit.Heart, Rank.Ace)),
            emptyList(),
            emptyList(),
            false,
            null,
            ConcurrentHashMap(),
            Instant.now()
        )
        val projection2 = PlayerView(
            PlayerViewKey(player2, game),
            listOf(Card(Suit.Pike, Rank.King)),
            emptyList(),
            emptyList(),
            false,
            null,
            ConcurrentHashMap(),
            Instant.now()
        )

        // When
        repository.save(projection1)
        repository.save(projection2)

        // Then
        val result1 = repository.getByKey(PlayerViewKey(player1, game))
        val result2 = repository.getByKey(PlayerViewKey(player2, game))
        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)
        assertEquals(1, result1.getOrThrow().cards.size)
        assertEquals(1, result2.getOrThrow().cards.size)
    }

    @Test
    fun `given repository with projections when clear called then all projections removed`() = runBlocking {
        // Given
        val key = PlayerViewKey(PlayerIdentity.create(), GameIdentity.create())
        val projection = PlayerView(
            key,
            listOf(Card(Suit.Heart, Rank.Ace)),
            emptyList(),
            emptyList(),
            false,
            null,
            ConcurrentHashMap(),
            Instant.now()
        )
        repository.save(projection)

        // When
        repository.clear()

        // Then
        assertTrue(repository.getAllProjections().isEmpty())
    }

    @Test
    fun `given repository when emptyProjection called then return empty PlayerView`() {
        // Given
        val playerIdentity = PlayerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val key = PlayerViewKey(playerIdentity, gameIdentity)

        // When
        val emptyProjection = repository.emptyProjection(key)

        // Then
        assertEquals(playerIdentity, emptyProjection.key.playerIdentity)
        assertEquals(gameIdentity, emptyProjection.key.gameIdentity)
        assertTrue(emptyProjection.cards.isEmpty())
    }
}
