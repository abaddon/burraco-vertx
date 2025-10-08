package com.abaddon83.burraco.player.adapter.queryController

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.player.port.QueryControllerPort
import com.abaddon83.burraco.player.projection.playerview.PlayerView
import com.abaddon83.burraco.player.projection.playerview.PlayerViewKey
import com.abaddon83.burraco.player.projection.playerview.PlayerViewRepository

class QueryControllerAdapter(
    private val playerViewRepository: PlayerViewRepository
) : QueryControllerPort {

    override suspend fun getPlayerView(playerIdentity: PlayerIdentity, gameIdentity: GameIdentity): Result<PlayerView> {
        val key = PlayerViewKey(playerIdentity, gameIdentity)
        return playerViewRepository.getByKey(key)
    }
}
