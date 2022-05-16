package com.abaddon83.burraco.game.models

import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.card.Ranks
import com.abaddon83.burraco.game.models.card.Suits
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class StraightTest{
    @Test
    fun `Given a Straight without jolly when calculate score then it is 200`() {
        val expectedScore = 200
        val cards = listOf(
            Card(Suits.Heart,Ranks.Three),
            Card(Suits.Heart,Ranks.Four),
            Card(Suits.Heart,Ranks.Five),
            Card(Suits.Heart,Ranks.Six),
            Card(Suits.Heart,Ranks.Seven),
            Card(Suits.Heart,Ranks.Eight),
            Card(Suits.Heart,Ranks.Nine),
        ).shuffled()

        val straight = Straight.create(StraightIdentity.create(),cards)

        assertEquals(expectedScore,straight.score())
    }

    @Test
    fun `Given a Straight with a Two as card and with the same Suit when calculate score then it is 200`() {
        val expectedScore = 200
        val cards = listOf(
            Card(Suits.Heart,Ranks.Ace),
            Card(Suits.Heart,Ranks.Two),
            Card(Suits.Heart,Ranks.Three),
            Card(Suits.Heart,Ranks.Four),
            Card(Suits.Heart,Ranks.Five),
            Card(Suits.Heart,Ranks.Six),
            Card(Suits.Heart,Ranks.Seven),
            Card(Suits.Heart,Ranks.Eight),
        ).shuffled()

        val straight = Straight.create(StraightIdentity.create(),cards)

        assertEquals(expectedScore,straight.score())
    }

    @Test
    fun `Given a Straight with a Two as jolly and card with the same Suit when calculate score then it is 100`() {
        val expectedScore = 100
        val cards = listOf(
            Card(Suits.Heart,Ranks.Ace),
            Card(Suits.Heart,Ranks.Two),
            Card(Suits.Heart,Ranks.Three),
            Card(Suits.Heart,Ranks.Four),
            Card(Suits.Heart,Ranks.Five),
            Card(Suits.Heart,Ranks.Six),
            Card(Suits.Heart,Ranks.Two)

        ).shuffled()

        val straight = Straight.create(StraightIdentity.create(),cards)

        assertEquals(expectedScore,straight.score())
    }

    @Test
    fun `Given a Straight with a Two as jolly and card with the same Suit when calculate score then it is 150`() {
        val expectedScore = 150
        val cards = listOf(
            Card(Suits.Heart,Ranks.Ace),
            Card(Suits.Heart,Ranks.Two),
            Card(Suits.Heart,Ranks.Three),
            Card(Suits.Heart,Ranks.Four),
            Card(Suits.Heart,Ranks.Five),
            Card(Suits.Heart,Ranks.Six),
            Card(Suits.Heart,Ranks.Seven),
            Card(Suits.Heart,Ranks.Two)

        ).shuffled()

        val straight = Straight.create(StraightIdentity.create(),cards)

        assertEquals(expectedScore,straight.score())
    }

    @Test
    fun `Given a Straight with a jolly when calculate score then it is 100`() {
        val expectedScore = 100
        val cards = listOf(
            Card(Suits.Heart,Ranks.Ace),
            Card(Suits.Heart,Ranks.Two),
            Card(Suits.Heart,Ranks.Three),
            Card(Suits.Heart,Ranks.Four),
            Card(Suits.Heart,Ranks.Five),
            Card(Suits.Jolly,Ranks.Jolly),
            Card(Suits.Heart,Ranks.Seven)
        ).shuffled()

        val straight = Straight.create(StraightIdentity.create(),cards)

        assertEquals(expectedScore,straight.score())
    }

    @Test
    fun `Given a Straight with a jolly when calculate score then it is 150`() {
        val expectedScore = 150
        val cards = listOf(
            Card(Suits.Heart,Ranks.Ace),
            Card(Suits.Heart,Ranks.Two),
            Card(Suits.Heart,Ranks.Three),
            Card(Suits.Heart,Ranks.Four),
            Card(Suits.Heart,Ranks.Five),
            Card(Suits.Heart,Ranks.Six),
            Card(Suits.Heart,Ranks.Seven),
            Card(Suits.Jolly,Ranks.Jolly),
            Card(Suits.Heart,Ranks.Eight),
        ).shuffled()

        val straight = Straight.create(StraightIdentity.create(),cards)

        assertEquals(expectedScore,straight.score())
    }
}