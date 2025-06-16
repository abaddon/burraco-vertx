package com.abaddon83.burraco.game.events.game

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.TrisIdentity
import com.abaddon83.burraco.common.models.card.Rank
import com.abaddon83.burraco.common.models.card.Suit
import com.abaddon83.burraco.game.models.Tris
import com.abaddon83.burraco.game.models.card.Card
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test

internal class TrisDroppedTest {
    @Test
    fun `given a valid element when serialised then Json is produced`() {
        val event = TrisDropped.create(
            GameIdentity.create(),
            PlayerIdentity.create(),
            Tris.create(
                TrisIdentity.create(),
                listOf(Card(Suit.Heart, Rank.Ace), Card(Suit.Clover, Rank.Ace), Card(Suit.Heart, Rank.Ace))
            )
        )
        assertDoesNotThrow {
            event.toJson()
        }
    }
}