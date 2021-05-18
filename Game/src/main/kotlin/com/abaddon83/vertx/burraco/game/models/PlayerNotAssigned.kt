package com.abaddon83.vertx.burraco.game.models

import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.vertx.burraco.game.models.players.Player

data class PlayerNotAssigned(override val identity: PlayerIdentity): Player() {
}
