package com.abaddon83.burraco.game.helpers


import com.abaddon83.burraco.common.models.card.Rank
import com.abaddon83.burraco.common.models.card.Suit
import com.abaddon83.burraco.game.models.card.Card
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class CardsFunctionsKtTest {

    @Test
    fun `Given a list of different cards when a card is removed then the list doesn't contain this card`() {
        val cardsToRemove = listOf(
            Card(Suit.Heart, Rank.Ace)
        )
        val expectedList = listOf(
            Card(Suit.Heart, Rank.Eight),
            Card(Suit.Heart, Rank.Seven),
            Card(Suit.Heart, Rank.Six),
            Card(Suit.Heart, Rank.Five),
            Card(Suit.Heart, Rank.Four),
            Card(Suit.Heart, Rank.Three),
            Card(Suit.Heart, Rank.Two)
        )
        val list = expectedList.plus(cardsToRemove)

        val actualList = list.removeCards(cardsToRemove)
        assertEquals(expectedList, actualList)
    }

    @Test
    fun `Given a list of different cards when 2 cards are removed then the list doesn't contain these cards`() {
        val cardsToRemove = listOf(
            Card(Suit.Heart, Rank.Ace),
            Card(Suit.Pike, Rank.Ace)
        )
        val expectedList = listOf(
            Card(Suit.Heart, Rank.Eight),
            Card(Suit.Heart, Rank.Seven),
            Card(Suit.Heart, Rank.Six),
            Card(Suit.Heart, Rank.Five),
            Card(Suit.Heart, Rank.Four),
            Card(Suit.Heart, Rank.Three),
            Card(Suit.Heart, Rank.Two)
        )
        val list = expectedList.plus(cardsToRemove)

        val actualList = list.removeCards(cardsToRemove)
        assertEquals(expectedList, actualList)
    }

    @Test
    fun `Given a list of equals cards when 1 card is removed then only one card is removed`() {
        val cardsToRemove = listOf(
            Card(Suit.Heart, Rank.Ace)
        )
        val expectedList = listOf(
            Card(Suit.Heart, Rank.Ace),
            Card(Suit.Heart, Rank.Ace),
            Card(Suit.Heart, Rank.Ace)
        )
        val list = expectedList.plus(cardsToRemove)

        val actualList = list.removeCards(cardsToRemove)
        assertEquals(expectedList, actualList)
    }

    @Test
    fun `Given a list of equals cards when 2 cards are removed then only two cards are removed`() {
        val cardsToRemove = listOf(
            Card(Suit.Heart, Rank.Ace),
            Card(Suit.Heart, Rank.Ace)
        )
        val expectedList = listOf(
            Card(Suit.Heart, Rank.Ace),
            Card(Suit.Heart, Rank.Ace),
            Card(Suit.Heart, Rank.Ace)
        )
        val list = expectedList.plus(cardsToRemove)

        val actualList = list.removeCards(cardsToRemove)
        assertEquals(expectedList, actualList)
    }

    @Test
    fun `Given a list of cards with no jolly when it's checked then result is false`() {
        val expected = false
        val list = listOf(
            Card(Suit.Heart, Rank.Ace),
            Card(Suit.Heart, Rank.Ace)
        )
        val actual = list.containsJolly()
        assertEquals(expected, actual)
    }

    @Test
    fun `Given a list of cards with a jolly when it's checked then result is true`() {
        val expected = true
        val list = listOf(
            Card(Suit.Heart, Rank.Ace),
            Card(Suit.Jolly, Rank.Jolly)
        )
        val actual = list.containsJolly()
        assertEquals(expected, actual)
    }

    @Test
    fun `Given a list of cards with 2 jollies when it's checked then result is true`() {
        val expected = true
        val list = listOf(
            Card(Suit.Heart, Rank.Ace),
            Card(Suit.Jolly, Rank.Jolly),
            Card(Suit.Jolly, Rank.Jolly)
        )
        val actual = list.containsJolly()
        assertEquals(expected, actual)
    }

    @Test
    fun `Given a list of cards with 2 K when calculate the score the result is 20`() {
        val expected = 20
        val list = listOf(
            Card(Suit.Heart, Rank.King),
            Card(Suit.Pike, Rank.King)
        )
        val actual = list.score()
        assertEquals(expected, actual)
    }

    @Test
    fun `Given a list of cards with one K, one 7, one Ace and one Jolly and one 2 when calculate the score the result is 50`() {
        val expected = 80
        val list = listOf(
            Card(Suit.Heart, Rank.King), //10
            Card(Suit.Pike, Rank.Seven), //5
            Card(Suit.Jolly, Rank.Jolly), //30
            Card(Suit.Pike, Rank.Ace), //15
            Card(Suit.Pike, Rank.Two) //20
        )
        val actual = list.score()
        assertEquals(expected, actual)
    }
}