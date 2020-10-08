package com.abaddon83.vertx.burraco.engine.ports

import com.abaddon83.vertx.burraco.engine.models.BurracoPlayer
import com.abaddon83.vertx.burraco.engine.models.PlayerNotAssigned
import com.abaddon83.vertx.burraco.engine.models.burracoGameExecutions.playerInGames.PlayerInGame
import com.abaddon83.vertx.burraco.engine.models.players.PlayerIdentity

interface PlayerPort {
        fun findPlayerNotAssignedBy(playerIdentity: PlayerIdentity): PlayerNotAssigned
        fun findBurracoPlayerBy(playerIdentity: PlayerIdentity): BurracoPlayer
        fun findBurracoPlayerInGameBy(playerIdentity: PlayerIdentity): PlayerInGame
}