package com.abaddon83.burraco.player.projection.gameview

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.player.projection.GameState
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

internal class GameViewRepositoryTest {

    private lateinit var repository: InMemoryGameViewRepository

    @BeforeEach
    fun setUp() {
        repository = InMemoryGameViewRepository()
    }

    @Test
    fun `Given empty repository when getByKey called then return empty projection`() = runBlocking {
        val gameIdentity = GameIdentity.create()
        val key = GameViewKey(gameIdentity)

        val result = repository.getByKey(key)

        assertTrue(result.isSuccess)
        val gameView = result.getOrThrow()
        assertEquals(GameViewKey(GameIdentity.empty()), gameView.key)
        assertTrue(gameView.players.isEmpty())
        assertEquals(GameState.DRAFT, gameView.state)
    }

    @Test
    fun `Given projection saved when getByKey called then return saved projection`() = runBlocking {
        val gameIdentity = GameIdentity.create()
        val key = GameViewKey(gameIdentity)
        val originalGameView = GameView.empty().copy(key = key)

        repository.save(originalGameView)
        val result = repository.getByKey(key)

        assertTrue(result.isSuccess)
        val retrievedGameView = result.getOrThrow()
        assertEquals(key, retrievedGameView.key)
    }

    @Test
    fun `Given multiple projections when save called then all stored correctly`() = runBlocking {
        val gameIdentity1 = GameIdentity.create()
        val gameIdentity2 = GameIdentity.create()
        val key1 = GameViewKey(gameIdentity1)
        val key2 = GameViewKey(gameIdentity2)
        val gameView1 = GameView.empty().copy(key = key1)
        val gameView2 = GameView.empty().copy(key = key2)

        repository.save(gameView1)
        repository.save(gameView2)

        val result1 = repository.getByKey(key1)
        val result2 = repository.getByKey(key2)

        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)
        assertEquals(key1, result1.getOrThrow().key)
        assertEquals(key2, result2.getOrThrow().key)
        assertEquals(2, repository.getAllProjections().size)
    }

    @Test
    fun `Given repository with projections when clear called then all projections removed`() = runBlocking {
        val gameIdentity = GameIdentity.create()
        val key = GameViewKey(gameIdentity)
        val gameView = GameView.empty().copy(key = key)

        repository.save(gameView)
        assertEquals(1, repository.getAllProjections().size)

        repository.clear()

        assertEquals(0, repository.getAllProjections().size)
        // Should return empty projection for any key after clear
        val result = repository.getByKey(key)
        assertTrue(result.isSuccess)
        assertEquals(GameViewKey(GameIdentity.empty()), result.getOrThrow().key)
    }

    @Test
    fun `Given repository when emptyProjection called then return empty GameView`() {
        val gameIdentity = GameIdentity.create()
        val key = GameViewKey(gameIdentity)

        val emptyProjection = repository.emptyProjection(key)

        assertEquals(GameViewKey(GameIdentity.empty()), emptyProjection.key)
        assertTrue(emptyProjection.players.isEmpty())
        assertEquals(GameState.DRAFT, emptyProjection.state)
        assertNotNull(emptyProjection.lastProcessedEvent)
        assertNotNull(emptyProjection.lastUpdated)
    }

    // ============ NEW TESTS FOR FINDBYID METHOD ============

    @Test
    fun `Given empty repository when findByGameId called then return null`() = runBlocking {
        val gameIdentity = GameIdentity.create()

        val result = repository.findByGameId(gameIdentity)

        assertNull(result)
    }

    @Test
    fun `Given repository with GameView when findByGameId called with correct id then return GameView`() = runBlocking {
        val gameIdentity = GameIdentity.create()
        val playerIdentity = PlayerIdentity.create()
        val gameView = GameView(
            key = GameViewKey(gameIdentity),
            players = listOf(playerIdentity),
            maxPlayers = 4,
            state = GameState.DRAFT,
            lastProcessedEvent = ConcurrentHashMap(),
            lastUpdated = Instant.now()
        )

        repository.save(gameView)
        val result = repository.findByGameId(gameIdentity)

        assertNotNull(result)
        assertEquals(gameIdentity, result!!.key.gameIdentity)
        assertEquals(1, result.players.size)
        assertTrue(result.players.contains(playerIdentity))
        assertEquals(4, result.maxPlayers)
        assertEquals(GameState.DRAFT, result.state)
    }

    @Test
    fun `Given repository with multiple GameViews when findByGameId called then return correct GameView`() = runBlocking {
        val gameIdentity1 = GameIdentity.create()
        val gameIdentity2 = GameIdentity.create()
        val gameIdentity3 = GameIdentity.create()

        val gameView1 = GameView(
            key = GameViewKey(gameIdentity1),
            players = listOf(PlayerIdentity.create()),
            maxPlayers = 4,
            state = GameState.DRAFT,
            lastProcessedEvent = ConcurrentHashMap(),
            lastUpdated = Instant.now()
        )

        val gameView2 = GameView(
            key = GameViewKey(gameIdentity2),
            players = listOf(PlayerIdentity.create(), PlayerIdentity.create()),
            maxPlayers = 4,
            state = GameState.PLAYING,
            lastProcessedEvent = ConcurrentHashMap(),
            lastUpdated = Instant.now()
        )

        repository.save(gameView1)
        repository.save(gameView2)

        val result1 = repository.findByGameId(gameIdentity1)
        val result2 = repository.findByGameId(gameIdentity2)
        val result3 = repository.findByGameId(gameIdentity3)

        assertNotNull(result1)
        assertNotNull(result2)
        assertNull(result3)

        assertEquals(gameIdentity1, result1!!.key.gameIdentity)
        assertEquals(1, result1.players.size)
        assertEquals(GameState.DRAFT, result1.state)

        assertEquals(gameIdentity2, result2!!.key.gameIdentity)
        assertEquals(2, result2.players.size)
        assertEquals(GameState.PLAYING, result2.state)
    }

    @Test
    fun `Given repository with empty GameView when findByGameId called then return null`() = runBlocking {
        val gameIdentity = GameIdentity.create()
        val emptyGameView = GameView.empty()

        repository.save(emptyGameView)
        val result = repository.findByGameId(gameIdentity)

        assertNull(result) // Should not return empty projections
    }
}