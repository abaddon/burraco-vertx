package com.abaddon83.burraco.game.events.game

import com.abaddon83.burraco.common.models.card.Card
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.event.game.CardAddedDeck
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test

internal class CardAddedDeckTest{

    @Test
    fun `given a valid element when serialised then Json is produced`(){
        val event = CardAddedDeck.create(GameIdentity.create(), Card.jolly())
        assertDoesNotThrow{
              event.toJson()
        }
    }
}