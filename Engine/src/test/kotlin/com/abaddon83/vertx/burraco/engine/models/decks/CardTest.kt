package com.abaddon83.vertx.burraco.engine.models.decks

import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.assertEquals

class CardTest {

    @Test
    fun `given a Card when I serialise it, then I should have the same Card deserialized`() {

        val card: Card = Card(suit = Suits.Clover, rank = Ranks.Ace);

        val jsonString = Json.encodeToString(CardCustomSerializer,card);
        val deserializedCard = Json.decodeFromString<Card>(CardCustomSerializer,jsonString)
        assertEquals(card.suit,deserializedCard.suit)
        assertEquals(card.rank,deserializedCard.rank)
    }
}