package com.abaddon83.burraco.common.models.valueObjects

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.assertEquals

class CardTest {

    @Test
    fun `given a Card when I serialise it, then I should have the same Card deserialized`() {

        val card: Card = Card(suit = Suits.Clover, rank = Ranks.Ace);

        val jsonString = Json.encodeToString(card);
        val deserializedCard = Json.decodeFromString<Card>(jsonString)
        assertEquals(card.suit,deserializedCard.suit)
        assertEquals(card.rank,deserializedCard.rank)
    }
}