package com.abaddon83.burraco.game.events.game

import com.abaddon83.burraco.game.models.TrisIdentity
import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.game.GameIdentity
import com.abaddon83.burraco.game.models.player.PlayerIdentity
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class CardsAddedToTrisTest{
    @Test
    fun `given a valid element when serialised then Json is produced`(){
        val event = CardsAddedToTris.create(GameIdentity.create(), PlayerIdentity.create(), TrisIdentity.create(), listOf( Card.jolly()))
        assertDoesNotThrow{
            event.toJson()
        }
    }
}