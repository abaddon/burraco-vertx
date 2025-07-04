package com.abaddon83.burraco.game.events.game

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.event.game.GameEnded
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class GameEndedTest{
    @Test
    fun `given a valid element when serialised then Json is produced`(){
        val event = GameEnded.create(
            GameIdentity.create())
        assertDoesNotThrow{
            event.toJson()
        }
    }
}