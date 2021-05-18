package com.abaddon83.vertx.burraco.game.ports

import com.abaddon83.vertx.burraco.game.models.BurracoPlayer
import com.abaddon83.vertx.burraco.game.models.PlayerNotAssigned
import com.abaddon83.vertx.burraco.game.models.burracoGameExecutions.playerInGames.PlayerInGame
import com.abaddon83.burraco.common.models.identities.PlayerIdentity

interface PlayerPort {
        fun findPlayerNotAssignedBy(playerIdentity: PlayerIdentity): PlayerNotAssigned
        fun findBurracoPlayerBy(playerIdentity: PlayerIdentity): BurracoPlayer
        fun findBurracoPlayerInGameBy(playerIdentity: PlayerIdentity): PlayerInGame
}