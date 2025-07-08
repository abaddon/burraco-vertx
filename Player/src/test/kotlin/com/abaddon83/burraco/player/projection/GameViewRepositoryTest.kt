package com.abaddon83.burraco.player.projection

import com.abaddon83.burraco.common.models.GameIdentity
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GameViewRepositoryTest {

    private lateinit var repository: GameViewRepository

    @BeforeEach
    fun setUp() {
        repository = GameViewRepository()
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
}