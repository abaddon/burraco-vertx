package com.abaddon83.burraco.player.projection.gameview

import io.github.abaddon.kcqrs.core.persistence.IProjectionRepository
import io.github.abaddon.kcqrs.core.projections.IProjectionKey
import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log
import java.util.concurrent.ConcurrentHashMap

class GameViewRepository : IProjectionRepository<GameView> {
    
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
}