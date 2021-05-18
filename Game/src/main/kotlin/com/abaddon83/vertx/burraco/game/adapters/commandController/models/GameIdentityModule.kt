package com.abaddon83.vertx.burraco.game.adapters.commandController.models

import com.abaddon83.burraco.common.models.identities.GameIdentity

data class GameIdentityModule(val code: String, val type: String) {

    constructor(gameIdentity: GameIdentity) : this(
            code = gameIdentity.convertTo().toString(),
            type = gameIdentity.convertTo().javaClass.simpleName
    )
}