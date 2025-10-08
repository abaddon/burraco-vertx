package com.abaddon83.burraco.player.projection.playerview

import io.github.abaddon.kcqrs.core.persistence.IProjectionRepository
import io.github.abaddon.kcqrs.core.projections.IProjectionKey
import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log
import java.util.concurrent.ConcurrentHashMap

class PlayerViewRepository : IProjectionRepository<PlayerView> {

    private val projections = ConcurrentHashMap<String, PlayerView>()

    override suspend fun getByKey(key: IProjectionKey): Result<PlayerView> = runCatching {
        val projectionKey = key.key()
        log.debug("Getting PlayerView projection by key: $projectionKey")

        projections[projectionKey] ?: emptyProjection(key)
    }

    override suspend fun save(projection: PlayerView): Result<Unit> = runCatching {
        val key = projection.key.key()
        log.debug("Saving PlayerView projection with key: $key")

        projections[key] = projection
        log.info("PlayerView projection saved successfully with key: $key")
    }

    override fun emptyProjection(key: IProjectionKey): PlayerView {
        log.debug("Creating empty PlayerView projection for key: ${key.key()}")
        require(key is PlayerViewKey) { "Key must be PlayerViewKey" }
        return PlayerView.empty().copy(key = key)
    }

    fun getAllProjections(): Map<String, PlayerView> = projections.toMap()

    fun clear() {
        log.debug("Clearing all PlayerView projections")
        projections.clear()
    }
}
