package com.abaddon83.vertx.burraco.game.models

import com.abaddon83.burraco.common.models.identities.GameIdentity
import org.junit.Test

class BurracoGameTest {

    @Test
    fun `Given anew game identity, when I create a new Burraco game, then I have a new game`(){
        val gameId= GameIdentity.create()
        val game = BurracoGame.create(gameId)
        assert(game.numPlayers() == 0)
        assert(game.identity() == gameId)
    }
}