package com.abaddon83.burraco.game.models

import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameIdentity
import org.junit.jupiter.api.Test

class BurracoGameTest {

    @Test
    fun `Given anew game identity, when I create a new Burraco game, then I have a new game`(){
        val gameId= GameIdentity.create()
        val game = Game.create(gameId)
        assert(game.numPlayers() == 0)
        assert(game.identity() == gameId)
    }
}