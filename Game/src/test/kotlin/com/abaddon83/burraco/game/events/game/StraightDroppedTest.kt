package com.abaddon83.burraco.game.events.game

import com.abaddon83.burraco.game.models.Straight
import com.abaddon83.burraco.common.models.StraightIdentity
import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.card.Rank
import com.abaddon83.burraco.common.models.card.Suit
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class StraightDroppedTest{
    @Test
    fun `given a valid element when serialised then Json is produced`(){
        val event = StraightDropped.create(
            GameIdentity.create(), PlayerIdentity.create(), Straight.create(StraightIdentity.create(), listOf(
                Card(Suit.Heart, Rank.Ace),
                Card(Suit.Heart, Rank.Two),
                Card(Suit.Heart, Rank.Three)
            )))
        assertDoesNotThrow{
            println(event.toJson())
        }
    }
}