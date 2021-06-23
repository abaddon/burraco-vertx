package com.abaddon83.burraco.common.models.entities

import com.abaddon83.burraco.common.models.identities.BurracoIdentity
import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.burraco.common.models.valueObjects.Ranks
import com.abaddon83.burraco.common.models.valueObjects.Suits
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


internal class BurracoTest{

    class BurracoImpl(override val identity: BurracoIdentity, override val cards: List<Card>) : Burraco() {}

    @Test
    fun `given a burraco with 7 cards when I ask the list of cards then I should see a list of 7 cards`() {
        val burraco = BurracoImpl(BurracoIdentity.create(),get7Cards())
        assertEquals(7, burraco.showCards().size)
    }

    @Test
    fun `given a burraco with 7 cards when I ask if is a burraco I should receive a positive answer`() {
        val burraco = BurracoImpl(BurracoIdentity.create(),get7Cards())
        assertEquals(true, burraco.isBurraco())
    }

    @Test
    fun `given a burraco with 6 cards when I ask if is a burraco I should receive a negative answer`() {
        val burraco = BurracoImpl(BurracoIdentity.create(),get6Cards())
        assertEquals(false, burraco.isBurraco())
    }

    private fun get7Cards(): List<Card>{
        return listOf(
            Card(Suits.Heart, Ranks.Three),
            Card(Suits.Heart, Ranks.Six),
            Card(Suits.Heart, Ranks.Five),
            Card(Suits.Heart, Ranks.Seven),
            Card(Suits.Heart, Ranks.Five),
            Card(Suits.Heart, Ranks.Eight),
            Card(Suits.Heart, Ranks.Ace)
        )
    }

    private fun get6Cards(): List<Card>{
        return listOf(
            Card(Suits.Heart, Ranks.Three),
            Card(Suits.Heart, Ranks.Six),
            Card(Suits.Heart, Ranks.Five),
            Card(Suits.Heart, Ranks.Five),
            Card(Suits.Heart, Ranks.Eight),
            Card(Suits.Heart, Ranks.Ace)
        )
    }
}