package com.abaddon83.burraco.game.events.game

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.event.game.NextPlayerTurnStarted
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class NextPlayerTurnStartedTest{
    @Test
    fun `given a valid element when serialised then Json is produced`(){
        val event = NextPlayerTurnStarted.create(
            GameIdentity.create(), PlayerIdentity.create())
        assertDoesNotThrow{
            event.toJson()
        }
    }
}