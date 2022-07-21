package com.abaddon83.burraco.game.events.game

import com.abaddon83.burraco.game.models.Straight
import com.abaddon83.burraco.common.models.StraightIdentity
import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.card.Ranks
import com.abaddon83.burraco.game.models.card.Suits
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class StraightDroppedTest{
    @Test
    fun `given a valid element when serialised then Json is produced`(){
        val event = StraightDropped.create(
            GameIdentity.create(), PlayerIdentity.create(), Straight.create(StraightIdentity.create(), listOf(
                Card(Suits.Heart, Ranks.Ace),
                Card(Suits.Heart, Ranks.Two),
                Card(Suits.Heart, Ranks.Three)
            )))
        assertDoesNotThrow{
            println(event.toJson())
        }
    }
}