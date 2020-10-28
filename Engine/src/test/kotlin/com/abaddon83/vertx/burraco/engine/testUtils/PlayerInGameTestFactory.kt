package com.abaddon83.vertx.burraco.engine.testUtils

import com.abaddon83.vertx.burraco.engine.models.BurracoScale
import com.abaddon83.vertx.burraco.engine.models.BurracoTris
import com.abaddon83.vertx.burraco.engine.models.burracoGameExecutions.playerInGames.PlayerInGame
import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.burraco.common.models.identities.PlayerIdentity

data class PlayerInGameTestFactory(val playerInGame: PlayerInGame) {

    fun withCards(cards: List<Card>): PlayerInGameTestFactory =
            this.copy(playerInGame = playerInGame.copy(cards = cards))

    fun withTrisOnTable(burracoTris: BurracoTris): PlayerInGameTestFactory =
            this.copy(playerInGame = playerInGame.dropATris(burracoTris))


    fun withScalaOnTable(burracoScale: BurracoScale): PlayerInGameTestFactory =
            this.copy(playerInGame = playerInGame.dropAScale(burracoScale))

    fun build(): PlayerInGame = playerInGame

    companion object Factory {
        fun create(): PlayerInGameTestFactory = create(PlayerIdentity.create())
        fun create(playerIdentity: PlayerIdentity): PlayerInGameTestFactory =
                PlayerInGameTestFactory(playerInGame = PlayerInGame.create(playerIdentity, listOf()))
    }
}