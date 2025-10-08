package com.abaddon83.burraco.player.port

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.player.projection.playerview.PlayerView

interface QueryControllerPort {
    suspend fun getPlayerView(playerIdentity: PlayerIdentity, gameIdentity: GameIdentity): Result<PlayerView>
}
