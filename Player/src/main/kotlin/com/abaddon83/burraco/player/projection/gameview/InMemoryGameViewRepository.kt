package com.abaddon83.burraco.player.projection.gameview

import com.abaddon83.burraco.common.models.GameIdentity
import io.github.abaddon.kcqrs.core.persistence.IProjectionRepository
import io.github.abaddon.kcqrs.core.projections.IProjectionKey
import io.github.abaddon.kcqrs.core.helpers.KcqrsLoggerFactory.log
import java.util.concurrent.ConcurrentHashMap

class InMemoryGameViewRepository : IProjectionRepository<GameView> {
    
    private val projections = ConcurrentHashMap<String, GameView>()
    
    override suspend fun getByKey(key: IProjectionKey): Result<GameView> = runCatching {
        val projectionKey = key.key()
        log.debug("Getting GameView projection by key: $projectionKey")
        
        projections[projectionKey] ?: emptyProjection(key)
    }
    
    override suspend fun save(projection: GameView): Result<Unit> = runCatching {
        val key = projection.key.key()
        log.debug("Saving GameView projection with key: $key")
        
        projections[key] = projection
        log.info("GameView projection saved successfully with key: $key")
    }
    
    override fun emptyProjection(key: IProjectionKey): GameView {
        log.debug("Creating empty GameView projection for key: ${key.key()}")
        return GameView.empty()
    }
    
    fun getAllProjections(): Map<String, GameView> = projections.toMap()

    fun clear() {
        log.debug("Clearing all GameView projections")
        projections.clear()
    }

    /**
     * Finds a GameView projection by GameIdentity
     * @param gameId The game identity to search for
     * @return GameView if found, null otherwise
     */
    suspend fun findByGameId(gameId: GameIdentity): GameView? {
        val key = GameViewKey(gameId).key()
        log.debug("Finding GameView by gameId: ${gameId.valueAsString()}, key: $key")

        val gameView = projections[key]

        // Don't return empty projections
        if (gameView != null && gameView.key.gameIdentity != GameIdentity.empty()) {
            log.debug("Found GameView for gameId: ${gameId.valueAsString()}")
            return gameView
        }

        log.debug("GameView not found for gameId: ${gameId.valueAsString()}")
        return null
    }
}