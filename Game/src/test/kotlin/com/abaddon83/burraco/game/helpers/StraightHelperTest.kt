package com.abaddon83.burraco.game.helpers

import com.abaddon83.burraco.common.models.StraightIdentity
import com.abaddon83.burraco.common.models.card.Rank
import com.abaddon83.burraco.common.models.card.Suit
import com.abaddon83.burraco.game.models.Straight
import com.abaddon83.burraco.common.models.card.Card
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

internal class StraightHelperTest {
    @Test
    fun `Given a list #1 of cards when the Straight is created then the card should sorted properly`() {
        val expectedCards = listOf(
            Card(Suit.Heart, Rank.Eight),
            Card(Suit.Heart, Rank.Seven),
            Card(Suit.Heart, Rank.Six),
            Card(Suit.Heart, Rank.Five),
            Card(Suit.Heart, Rank.Four),
            Card(Suit.Heart, Rank.Three),
            Card(Suit.Heart, Rank.Two),
            Card(Suit.Heart, Rank.Ace)
        )

        val straight = Straight.create(StraightIdentity.create(), expectedCards.shuffled())

        assertEquals(expectedCards, straight.cards)
    }

    @Test
    fun `Given a list #2 of cards when the Straight is created then the card should sorted properly`() {
        val expectedCards = listOf(
            Card(Suit.Heart, Rank.Eight),
            Card(Suit.Heart, Rank.Seven),
            Card(Suit.Heart, Rank.Six),
            Card(Suit.Heart, Rank.Five),
            Card(Suit.Heart, Rank.Four),
            Card(Suit.Heart, Rank.Three),
            Card(Suit.Heart, Rank.Two)
        )

        val straight = Straight.create(StraightIdentity.create(), expectedCards.shuffled())

        assertEquals(expectedCards, straight.cards)
    }

    /* CLAUDE: the expected order [card: Pike-2, card: Heart-8, card: Heart-7, card: Heart-6, card: Heart-5, card: Heart-4, card: Heart-3, card: Heart-2] is valid because
    the card Pike-2 before the Heart-8 has to be considered as Jolly card, so the order is correct.
     */
    @Test
    fun `Given a list #3 of cards when the Straight is created then the card should sorted properly`() {
        val expectedCards = listOf(
            Card(Suit.Pike, Rank.Two),
            Card(Suit.Heart, Rank.Eight),
            Card(Suit.Heart, Rank.Seven),
            Card(Suit.Heart, Rank.Six),
            Card(Suit.Heart, Rank.Five),
            Card(Suit.Heart, Rank.Four),
            Card(Suit.Heart, Rank.Three),
            Card(Suit.Heart, Rank.Two)
        )

        val straight = Straight.create(StraightIdentity.create(), expectedCards.shuffled())

        assertEquals(expectedCards, straight.cards)
    }

    /* CLAUDE: the expected order [card: Jolly-J, card: Heart-8, card: Heart-7, card: Heart-6, card: Heart-5, card: Heart-4, card: Heart-3, card: Heart-2] is valid because
    the card Jolly-J before Heart-8 has to be considered valid but even the jolly carf after Heart-2 can be considered a valid sequence.
     */
    @Test
    fun `Given a list #4 of cards when the Straight is created then the card should sorted properly`() {
        val expectedCards = listOf(
            Card(Suit.Jolly, Rank.Jolly),
            Card(Suit.Heart, Rank.Eight),
            Card(Suit.Heart, Rank.Seven),
            Card(Suit.Heart, Rank.Six),
            Card(Suit.Heart, Rank.Five),
            Card(Suit.Heart, Rank.Four),
            Card(Suit.Heart, Rank.Three),
            Card(Suit.Heart, Rank.Two)
        )

        val straight = Straight.create(StraightIdentity.create(), expectedCards.shuffled())

        assertEquals(expectedCards, straight.cards)
    }

    @Test
    fun `Given a list #5 of cards when the Straight is created then the card should sorted properly`() {
        val expectedCards = listOf(
            Card(Suit.Heart, Rank.Eight),
            Card(Suit.Heart, Rank.Seven),
            Card(Suit.Heart, Rank.Six),
            Card(Suit.Heart, Rank.Five),
            Card(Suit.Heart, Rank.Four),
            Card(Suit.Jolly, Rank.Jolly),
            Card(Suit.Heart, Rank.Two)
        )

        val straight = Straight.create(StraightIdentity.create(), expectedCards.shuffled())

        assertEquals(expectedCards, straight.cards)
    }

    /* CLAUDE: the list of cards in the expectedCards is valid and can generate a Straight because one of the Heart-2 cards
    can be used as jolly and fix the Heart-6 gap.
     */
    @Test
    fun `Given a list #6 of cards when the Straight is created then the card should sorted properly`() {
        val expectedCards = listOf(
            Card(Suit.Heart, Rank.Seven),
            Card(Suit.Heart, Rank.Two),
            Card(Suit.Heart, Rank.Five),
            Card(Suit.Heart, Rank.Four),
            Card(Suit.Heart, Rank.Three),
            Card(Suit.Heart, Rank.Two),
            Card(Suit.Heart, Rank.Ace)
        )

        val straight = Straight.create(StraightIdentity.create(), expectedCards.shuffled())

        assertEquals(expectedCards, straight.cards)
    }

    /* CLAUDE: the expected order [card: Heart-2, card: Heart-6, card: Heart-5, card: Heart-4, card: Heart-3, card: Heart-2, card: Heart-A] is valid because
    the card Heart-2 before the Heart-6 has to be considered as Jolly card, so the order is correct.
     */
    @Test
    fun `Given a list #7 of cards when the Straight is created then the card should sorted properly`() {
        val expectedCards = listOf(
            Card(Suit.Heart, Rank.Two),
            Card(Suit.Heart, Rank.Six),
            Card(Suit.Heart, Rank.Five),
            Card(Suit.Heart, Rank.Four),
            Card(Suit.Heart, Rank.Three),
            Card(Suit.Heart, Rank.Two),
            Card(Suit.Heart, Rank.Ace)
        )

        val straight = Straight.create(StraightIdentity.create(), expectedCards.shuffled())

        assertEquals(expectedCards, straight.cards)
    }

    @Test
    fun `Given a wrong list #1 of cards when the Straight is created then exception raised`() {
        val expectedCards = listOf(
            Card(Suit.Heart, Rank.Nine),
            Card(Suit.Heart, Rank.Eight),
            Card(Suit.Heart, Rank.Seven),
            Card(Suit.Heart, Rank.Six),
            Card(Suit.Heart, Rank.Five),
            Card(Suit.Jolly, Rank.Jolly),
            Card(Suit.Heart, Rank.Two)
        )

        assertThrows(AssertionError::class.java) {
            Straight.create(StraightIdentity.create(), expectedCards.shuffled())
        }
    }
}