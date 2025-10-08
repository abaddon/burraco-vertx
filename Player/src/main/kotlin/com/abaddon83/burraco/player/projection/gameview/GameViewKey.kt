package com.abaddon83.burraco.player.projection.gameview

import com.abaddon83.burraco.common.models.GameIdentity
import io.github.abaddon.kcqrs.core.projections.IProjectionKey

data class GameViewKey(
    private val gameId: GameIdentity,
) : IProjectionKey {
    override fun key(): String = gameId.valueAsString()

    companion object {
        fun empty(): GameViewKey {
            return GameViewKey(GameIdentity.empty())
        }
    }
}
