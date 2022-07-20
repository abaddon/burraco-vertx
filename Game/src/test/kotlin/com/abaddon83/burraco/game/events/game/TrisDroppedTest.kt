package com.abaddon83.burraco.game.events.game

import com.abaddon83.burraco.game.models.Tris
import com.abaddon83.burraco.game.models.TrisIdentity
import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.card.Ranks
import com.abaddon83.burraco.game.models.card.Suits
import com.abaddon83.burraco.game.models.game.GameIdentity
import com.abaddon83.burraco.game.models.player.PlayerIdentity
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test

internal class TrisDroppedTest{
    @Test
    fun `given a valid element when serialised then Json is produced`(){
        val event = TrisDropped.create(
            GameIdentity.create(), PlayerIdentity.create(), Tris.create(TrisIdentity.create(), listOf(Card(Suits.Heart,Ranks.Ace),Card(Suits.Clover,Ranks.Ace),Card(Suits.Heart,Ranks.Ace))))
        assertDoesNotThrow{
            event.toJson()
        }
    }
}