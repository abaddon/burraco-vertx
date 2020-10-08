package com.abaddon83.vertx.burraco.engine.adapters.commandController.models


import com.abaddon83.vertx.burraco.engine.models.BurracoGame

data class GameModule(
    val identity: GameIdentityModule,
    val type: String
) {
    constructor(game: BurracoGame): this(
            identity = GameIdentityModule(game.identity()),
            type = "Burraco")
}