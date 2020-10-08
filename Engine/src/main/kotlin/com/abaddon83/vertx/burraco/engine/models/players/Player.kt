package com.abaddon83.vertx.burraco.engine.models.players

import com.abaddon83.utils.ddd.Entity

abstract class Player(className: String) : Entity<PlayerIdentity>(className) {
}