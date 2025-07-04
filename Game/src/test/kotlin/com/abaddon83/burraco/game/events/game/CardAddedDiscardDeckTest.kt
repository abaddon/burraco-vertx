package com.abaddon83.burraco.game.events.game

import com.abaddon83.burraco.common.models.card.Card
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.event.game.CardAddedDiscardDeck
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class CardAddedDiscardDeckTest{
    @Test
    fun `given a valid element when serialised then Json is produced`(){
        val event = CardAddedDiscardDeck.create(GameIdentity.create(), Card.jolly())
        assertDoesNotThrow{
            event.toJson()
        }
    }
}