package com.abaddon83.burraco.common.models.valueObjects

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CardTest {

    @Test
    fun `given a Card when I serialise it, then I should have the same Card deserialized`() {

        val card: Card = Card(suit = Suits.Clover, rank = Ranks.Ace);

        val jsonString = Json.encodeToString(card);
        println("card json: $jsonString")
        val deserializedCard = Json.decodeFromString<Card>(jsonString)
        assertEquals(card.suit,deserializedCard.suit)
        assertEquals(card.rank,deserializedCard.rank)
    }

    @Test
    fun `given a label when I create a card from it, then I should have a new card object with the same label`() {
        val label ="clover-A"
        val card: Card = Card.fromLabel(label)

        assertEquals(label,card.label)
    }

    @Test
    fun `given a wrong label when I create a card from it, then I should receive an error`() {
        val label ="fake-card"
        assertFailsWith (Exception::class,"The $label is not a valid Card"){
            Card.fromLabel(label)
        }
    }

    @Test
    fun `given a 2 cards when sort them, then I should see the cards well ordered`() {
        val cards = listOf(
            Card(Suits.Heart,Ranks.Three),
            Card(Suits.Heart,Ranks.Five),
            Card(Suits.Heart,Ranks.Ace)
        )
        val sorted = cards.sorted()
        assertEquals("heart-5",sorted.first().label)
        assertEquals("heart-A",sorted.last().label)
    }
}