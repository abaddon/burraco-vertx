package com.abaddon83.burraco.game.events.game

import com.abaddon83.burraco.common.models.card.Card
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.event.game.CardsPickedFromPlayerDeckDuringTurn
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class CardsPickedFromPlayerDeckDuringTurnTest{
    @Test
    fun `given a valid element when serialised then Json is produced`(){
        val event = CardsPickedFromPlayerDeckDuringTurn.create(GameIdentity.create(), PlayerIdentity.create(), listOf(Card.jolly()))
        assertDoesNotThrow{
            event.toJson()
        }
    }
}