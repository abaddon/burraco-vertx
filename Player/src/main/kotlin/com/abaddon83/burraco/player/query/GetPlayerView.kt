package com.abaddon83.burraco.player.query

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity

data class GetPlayerView(
    val playerIdentity: PlayerIdentity,
    val gameIdentity: GameIdentity
)
