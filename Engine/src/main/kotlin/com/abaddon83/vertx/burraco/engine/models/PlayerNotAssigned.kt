package com.abaddon83.vertx.burraco.engine.models

import com.abaddon83.vertx.burraco.engine.models.BurracoPlayer
import com.abaddon83.burraco.common.models.identities.PlayerIdentity

data class PlayerNotAssigned(override val identity: PlayerIdentity): BurracoPlayer("PlayerNotAssigned") {
}
