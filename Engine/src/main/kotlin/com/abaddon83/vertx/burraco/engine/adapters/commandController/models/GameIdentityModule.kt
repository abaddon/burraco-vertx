package com.abaddon83.vertx.burraco.engine.adapters.commandController.models

import com.abaddon83.vertx.burraco.engine.models.games.GameIdentity

data class GameIdentityModule(val code: String, val type: String) {

    constructor(gameIdentity: GameIdentity) : this(
            code = gameIdentity.convertTo().toString(),
            type = gameIdentity.convertTo().javaClass.simpleName
    )
}