package com.abaddon83.vertx.burraco.engine.adapters.commandController.bodyRequests

data class CreateGameRequest(val gameType: GameType) {

}

enum class GameType{ BURRACO }