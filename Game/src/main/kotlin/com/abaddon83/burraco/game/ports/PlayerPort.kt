package com.abaddon83.burraco.game.ports

import com.abaddon83.burraco.game.models.Player
import com.abaddon83.burraco.game.models.PlayerNotAssigned
import com.abaddon83.burraco.game.models.burracoGameExecutions.playerInGames.PlayerInGame
import com.abaddon83.burraco.common.models.identities.PlayerIdentity

interface PlayerPort {
        fun findPlayerNotAssignedBy(playerIdentity: PlayerIdentity): PlayerNotAssigned
        fun findBurracoPlayerBy(playerIdentity: PlayerIdentity): Player
        fun findBurracoPlayerInGameBy(playerIdentity: PlayerIdentity): PlayerInGame
}