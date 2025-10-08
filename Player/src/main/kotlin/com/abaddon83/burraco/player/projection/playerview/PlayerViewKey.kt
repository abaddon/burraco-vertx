package com.abaddon83.burraco.player.projection.playerview

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import io.github.abaddon.kcqrs.core.projections.IProjectionKey

data class PlayerViewKey(
    val playerIdentity: PlayerIdentity,
    val gameIdentity: GameIdentity
) : IProjectionKey {
    override fun key(): String = "${playerIdentity.identity}_${gameIdentity.identity}"

    companion object {
        fun empty(): PlayerViewKey = PlayerViewKey(PlayerIdentity.empty(), GameIdentity.empty())
    }
}
