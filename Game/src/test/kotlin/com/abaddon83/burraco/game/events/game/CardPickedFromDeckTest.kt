package com.abaddon83.burraco.game.events.game

import com.abaddon83.burraco.common.models.card.Card
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.event.game.CardPickedFromDeck
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class CardPickedFromDeckTest{
    @Test
    fun `given a valid element when serialised then Json is produced`(){
        val event = CardPickedFromDeck.create(GameIdentity.create(), PlayerIdentity.create(), Card.jolly())
        assertDoesNotThrow{
            event.toJson()
        }
    }
}