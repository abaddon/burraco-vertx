package com.abaddon83.burraco.game.events.game

import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.game.GameIdentity
import io.github.abaddon.kcqrs.core.domain.messages.events.EventHeader
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

internal class CardAddedSecondPlayerDeckTest{
    @Test
    fun `given a valid element when serialised then Json is produced`(){
        val event = CardAddedSecondPlayerDeck.create(GameIdentity.create(), Card.jolly())
        assertDoesNotThrow{
            event.toJson()
        }
    }
}