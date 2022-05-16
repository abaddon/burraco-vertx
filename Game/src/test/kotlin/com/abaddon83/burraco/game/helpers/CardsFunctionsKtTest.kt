package com.abaddon83.burraco.game.helpers


import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.card.Ranks
import com.abaddon83.burraco.game.models.card.Suits
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class CardsFunctionsKtTest {

    @Test
    fun `Given a list of different cards when a card is removed then the list doesn't contain this card`() {
        val cardsToRemove = listOf(
            Card(Suits.Heart, Ranks.Ace)
        )
        val expectedList = listOf(
            Card(Suits.Heart, Ranks.Eight),
            Card(Suits.Heart, Ranks.Seven),
            Card(Suits.Heart, Ranks.Six),
            Card(Suits.Heart, Ranks.Five),
            Card(Suits.Heart, Ranks.Four),
            Card(Suits.Heart, Ranks.Three),
            Card(Suits.Heart, Ranks.Two)
        )
        val list = expectedList.plus(cardsToRemove)

        val actualList = list.removeCards(cardsToRemove)
        assertEquals(expectedList, actualList)
    }

    @Test
    fun `Given a list of different cards when 2 cards are removed then the list doesn't contain these cards`() {
        val cardsToRemove = listOf(
            Card(Suits.Heart, Ranks.Ace),
            Card(Suits.Pike, Ranks.Ace)
        )
        val expectedList = listOf(
            Card(Suits.Heart, Ranks.Eight),
            Card(Suits.Heart, Ranks.Seven),
            Card(Suits.Heart, Ranks.Six),
            Card(Suits.Heart, Ranks.Five),
            Card(Suits.Heart, Ranks.Four),
            Card(Suits.Heart, Ranks.Three),
            Card(Suits.Heart, Ranks.Two)
        )
        val list = expectedList.plus(cardsToRemove)

        val actualList = list.removeCards(cardsToRemove)
        assertEquals(expectedList, actualList)
    }

    @Test
    fun `Given a list of equals cards when 1 card is removed then only one card is removed`() {
        val cardsToRemove = listOf(
            Card(Suits.Heart, Ranks.Ace)
        )
        val expectedList = listOf(
            Card(Suits.Heart, Ranks.Ace),
            Card(Suits.Heart, Ranks.Ace),
            Card(Suits.Heart, Ranks.Ace)
        )
        val list = expectedList.plus(cardsToRemove)

        val actualList = list.removeCards(cardsToRemove)
        assertEquals(expectedList, actualList)
    }

    @Test
    fun `Given a list of equals cards when 2 cards are removed then only two cards are removed`() {
        val cardsToRemove = listOf(
            Card(Suits.Heart, Ranks.Ace),
            Card(Suits.Heart, Ranks.Ace)
        )
        val expectedList = listOf(
            Card(Suits.Heart, Ranks.Ace),
            Card(Suits.Heart, Ranks.Ace),
            Card(Suits.Heart, Ranks.Ace)
        )
        val list = expectedList.plus(cardsToRemove)

        val actualList = list.removeCards(cardsToRemove)
        assertEquals(expectedList, actualList)
    }

    @Test
    fun `Given a list of cards with no jolly when it's checked then result is false`() {
        val expected = false
        val list = listOf(
            Card(Suits.Heart, Ranks.Ace),
            Card(Suits.Heart, Ranks.Ace)
        )
        val actual = list.containsJolly()
        assertEquals(expected, actual)
    }

    @Test
    fun `Given a list of cards with a jolly when it's checked then result is true`() {
        val expected = true
        val list = listOf(
            Card(Suits.Heart, Ranks.Ace),
            Card(Suits.Jolly, Ranks.Jolly)
        )
        val actual = list.containsJolly()
        assertEquals(expected, actual)
    }

    @Test
    fun `Given a list of cards with 2 jollies when it's checked then result is true`() {
        val expected = true
        val list = listOf(
            Card(Suits.Heart, Ranks.Ace),
            Card(Suits.Jolly, Ranks.Jolly),
            Card(Suits.Jolly, Ranks.Jolly)
        )
        val actual = list.containsJolly()
        assertEquals(expected, actual)
    }

    @Test
    fun `Given a list of cards with 2 K when calculate the score the result is 20`() {
        val expected = 20
        val list = listOf(
            Card(Suits.Heart, Ranks.King),
            Card(Suits.Pike, Ranks.King)
        )
        val actual = list.score()
        assertEquals(expected, actual)
    }

    @Test
    fun `Given a list of cards with one K, one 7, one Ace and one Jolly and one 2 when calculate the score the result is 50`() {
        val expected = 80
        val list = listOf(
            Card(Suits.Heart, Ranks.King), //10
            Card(Suits.Pike, Ranks.Seven), //5
            Card(Suits.Jolly, Ranks.Jolly), //30
            Card(Suits.Pike, Ranks.Ace), //15
            Card(Suits.Pike, Ranks.Two) //20
        )
        val actual = list.score()
        assertEquals(expected, actual)
    }
}