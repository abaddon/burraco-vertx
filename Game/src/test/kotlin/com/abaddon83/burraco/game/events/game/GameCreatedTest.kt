package com.abaddon83.burraco.game.events.game

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.event.game.GameCreated
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test

internal class GameCreatedTest{
    @Test
    fun `given a valid element when serialised then Json is produced`(){
        val event = GameCreated.create(
            GameIdentity.create())
        assertDoesNotThrow{
            event.toJson()
        }
    }
}