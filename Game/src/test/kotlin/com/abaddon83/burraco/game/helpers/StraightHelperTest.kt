package com.abaddon83.burraco.game.helpers

import com.abaddon83.burraco.game.models.Straight
import com.abaddon83.burraco.game.models.StraightIdentity
import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.card.Ranks
import com.abaddon83.burraco.game.models.card.Suits
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class StraightHelperTest{
    @Test
    fun `Given a list #1 of cards when the Straight is created then the card should sorted properly`(){
        val expectedCards = listOf(
            Card(Suits.Heart,Ranks.Eight),
            Card(Suits.Heart,Ranks.Seven),
            Card(Suits.Heart,Ranks.Six),
            Card(Suits.Heart,Ranks.Five),
            Card(Suits.Heart,Ranks.Four),
            Card(Suits.Heart,Ranks.Three),
            Card(Suits.Heart,Ranks.Two),
            Card(Suits.Heart,Ranks.Ace)
        )

        val straight = Straight.create(StraightIdentity.create(),expectedCards.shuffled())

        assertEquals(expectedCards,straight.cards)
    }

    @Test
    fun `Given a list #2 of cards when the Straight is created then the card should sorted properly`(){
        val expectedCards = listOf(
            Card(Suits.Heart,Ranks.Eight),
            Card(Suits.Heart,Ranks.Seven),
            Card(Suits.Heart,Ranks.Six),
            Card(Suits.Heart,Ranks.Five),
            Card(Suits.Heart,Ranks.Four),
            Card(Suits.Heart,Ranks.Three),
            Card(Suits.Heart,Ranks.Two)
        )

        val straight = Straight.create(StraightIdentity.create(),expectedCards.shuffled())

        assertEquals(expectedCards,straight.cards)
    }

    @Test
    fun `Given a list #3 of cards when the Straight is created then the card should sorted properly`(){
        val expectedCards = listOf(
            Card(Suits.Pike,Ranks.Two),
            Card(Suits.Heart,Ranks.Eight),
            Card(Suits.Heart,Ranks.Seven),
            Card(Suits.Heart,Ranks.Six),
            Card(Suits.Heart,Ranks.Five),
            Card(Suits.Heart,Ranks.Four),
            Card(Suits.Heart,Ranks.Three),
            Card(Suits.Heart,Ranks.Two)
        )

        val straight = Straight.create(StraightIdentity.create(),expectedCards.shuffled())

        assertEquals(expectedCards,straight.cards)
    }

    @Test
    fun `Given a list #4 of cards when the Straight is created then the card should sorted properly`(){
        val expectedCards = listOf(
            Card(Suits.Jolly,Ranks.Jolly),
            Card(Suits.Heart,Ranks.Eight),
            Card(Suits.Heart,Ranks.Seven),
            Card(Suits.Heart,Ranks.Six),
            Card(Suits.Heart,Ranks.Five),
            Card(Suits.Heart,Ranks.Four),
            Card(Suits.Heart,Ranks.Three),
            Card(Suits.Heart,Ranks.Two)
        )

        val straight = Straight.create(StraightIdentity.create(),expectedCards.shuffled())

        assertEquals(expectedCards,straight.cards)
    }

    @Test
    fun `Given a list #5 of cards when the Straight is created then the card should sorted properly`(){
        val expectedCards = listOf(
            Card(Suits.Heart,Ranks.Eight),
            Card(Suits.Heart,Ranks.Seven),
            Card(Suits.Heart,Ranks.Six),
            Card(Suits.Heart,Ranks.Five),
            Card(Suits.Heart,Ranks.Four),
            Card(Suits.Jolly,Ranks.Jolly),
            Card(Suits.Heart,Ranks.Two)
        )

        val straight = Straight.create(StraightIdentity.create(),expectedCards.shuffled())

        assertEquals(expectedCards,straight.cards)
    }

    @Test
    fun `Given a list #6 of cards when the Straight is created then the card should sorted properly`(){
        val expectedCards = listOf(
            Card(Suits.Heart,Ranks.Seven),
            Card(Suits.Heart,Ranks.Two),
            Card(Suits.Heart,Ranks.Five),
            Card(Suits.Heart,Ranks.Four),
            Card(Suits.Heart,Ranks.Three),
            Card(Suits.Heart,Ranks.Two),
            Card(Suits.Heart,Ranks.Ace)
        )

        val straight = Straight.create(StraightIdentity.create(),expectedCards.shuffled())

        assertEquals(expectedCards,straight.cards)
    }

    @Test
    fun `Given a list #7 of cards when the Straight is created then the card should sorted properly`(){
        val expectedCards = listOf(
            Card(Suits.Heart,Ranks.Two),
            Card(Suits.Heart,Ranks.Six),
            Card(Suits.Heart,Ranks.Five),
            Card(Suits.Heart,Ranks.Four),
            Card(Suits.Heart,Ranks.Three),
            Card(Suits.Heart,Ranks.Two),
            Card(Suits.Heart,Ranks.Ace)
        )

        val straight = Straight.create(StraightIdentity.create(),expectedCards.shuffled())

        assertEquals(expectedCards,straight.cards)
    }

    @Test
    fun `Given a wrong list #1 of cards when the Straight is created then exception raised`(){
        val expectedCards = listOf(
            Card(Suits.Heart,Ranks.Nine),
            Card(Suits.Heart,Ranks.Eight),
            Card(Suits.Heart,Ranks.Seven),
            Card(Suits.Heart,Ranks.Six),
            Card(Suits.Heart,Ranks.Five),
            Card(Suits.Jolly,Ranks.Jolly),
            Card(Suits.Heart,Ranks.Two)
        )

        assertThrows(AssertionError::class.java){
            Straight.create(StraightIdentity.create(),expectedCards.shuffled())
        }
    }
}