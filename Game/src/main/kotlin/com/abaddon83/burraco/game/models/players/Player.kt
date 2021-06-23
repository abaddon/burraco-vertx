package com.abaddon83.burraco.game.models.players

import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.utils.ddd.writeModel.Entity

abstract class Player() : Entity<PlayerIdentity>() {
}