package com.abaddon83.burraco.game.events.game

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.event.game.CardDealingRequested
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class CardDealingRequestedTest{
    @Test
    fun `given a valid element when serialised then Json is produced`(){
        val event = CardDealingRequested.create(GameIdentity.create(), PlayerIdentity.create())
        assertDoesNotThrow{
            event.toJson()
        }
    }
}