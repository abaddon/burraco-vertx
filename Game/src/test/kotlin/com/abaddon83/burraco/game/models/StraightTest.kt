package com.abaddon83.burraco.game.models

import com.abaddon83.burraco.common.models.StraightIdentity
import com.abaddon83.burraco.common.models.card.Rank
import com.abaddon83.burraco.common.models.card.Suit
import com.abaddon83.burraco.common.models.card.Card
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class StraightTest {
    @Test
    fun `Given a Straight without jolly when calculate score then it is 200`() {
        val expectedScore = 200
        val cards = listOf(
            Card(Suit.Heart, Rank.Three),
            Card(Suit.Heart, Rank.Four),
            Card(Suit.Heart, Rank.Five),
            Card(Suit.Heart, Rank.Six),
            Card(Suit.Heart, Rank.Seven),
            Card(Suit.Heart, Rank.Eight),
            Card(Suit.Heart, Rank.Nine),
        ).shuffled()

        val straight = Straight.create(StraightIdentity.create(), cards)

        assertEquals(expectedScore, straight.score())
    }

    @Test
    fun `Given a Straight with a Two as card and with the same Suit when calculate score then it is 200`() {
        val expectedScore = 200
        val cards = listOf(
            Card(Suit.Heart, Rank.Ace),
            Card(Suit.Heart, Rank.Two),
            Card(Suit.Heart, Rank.Three),
            Card(Suit.Heart, Rank.Four),
            Card(Suit.Heart, Rank.Five),
            Card(Suit.Heart, Rank.Six),
            Card(Suit.Heart, Rank.Seven),
            Card(Suit.Heart, Rank.Eight),
        ).shuffled()

        val straight = Straight.create(StraightIdentity.create(), cards)

        assertEquals(expectedScore, straight.score())
    }

    @Test
    fun `Given a Straight with a Two as jolly and card with the same Suit when calculate score then it is 100`() {
        val expectedScore = 100
        val cards = listOf(
            Card(Suit.Heart, Rank.Ace),
            Card(Suit.Heart, Rank.Two),
            Card(Suit.Heart, Rank.Three),
            Card(Suit.Heart, Rank.Four),
            Card(Suit.Heart, Rank.Five),
            Card(Suit.Heart, Rank.Six),
            Card(Suit.Heart, Rank.Two)

        ).shuffled()

        val straight = Straight.create(StraightIdentity.create(), cards)

        assertEquals(expectedScore, straight.score())
    }

    @Test
    fun `Given a Straight with a Two as jolly and card with the same Suit when calculate score then it is 150`() {
        val expectedScore = 150
        val cards = listOf(
            Card(Suit.Heart, Rank.Ace),
            Card(Suit.Heart, Rank.Two),
            Card(Suit.Heart, Rank.Three),
            Card(Suit.Heart, Rank.Four),
            Card(Suit.Heart, Rank.Five),
            Card(Suit.Heart, Rank.Six),
            Card(Suit.Heart, Rank.Seven),
            Card(Suit.Heart, Rank.Two)

        ).shuffled()

        val straight = Straight.create(StraightIdentity.create(), cards)

        assertEquals(expectedScore, straight.score())
    }

    @Test
    fun `Given a Straight with a jolly when calculate score then it is 100`() {
        val expectedScore = 100
        val cards = listOf(
            Card(Suit.Heart, Rank.Ace),
            Card(Suit.Heart, Rank.Two),
            Card(Suit.Heart, Rank.Three),
            Card(Suit.Heart, Rank.Four),
            Card(Suit.Heart, Rank.Five),
            Card(Suit.Jolly, Rank.Jolly),
            Card(Suit.Heart, Rank.Seven)
        ).shuffled()

        val straight = Straight.create(StraightIdentity.create(), cards)

        assertEquals(expectedScore, straight.score())
    }

    @Test
    fun `Given a Straight with a jolly when calculate score then it is 150`() {
        val expectedScore = 150
        val cards = listOf(
            Card(Suit.Heart, Rank.Ace),
            Card(Suit.Heart, Rank.Two),
            Card(Suit.Heart, Rank.Three),
            Card(Suit.Heart, Rank.Four),
            Card(Suit.Heart, Rank.Five),
            Card(Suit.Heart, Rank.Six),
            Card(Suit.Heart, Rank.Seven),
            Card(Suit.Jolly, Rank.Jolly),
            Card(Suit.Heart, Rank.Eight),
        ).shuffled()

        val straight = Straight.create(StraightIdentity.create(), cards)

        assertEquals(expectedScore, straight.score())
    }
}