package com.abaddon83.vertx.burraco.engine.models

import com.abaddon83.vertx.burraco.engine.models.BurracoPlayer
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.vertx.burraco.engine.models.players.Player

data class PlayerNotAssigned(override val identity: PlayerIdentity): Player() {
}
