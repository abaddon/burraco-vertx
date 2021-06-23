package com.abaddon83.burraco.common.models.valueObjects

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

internal class SuitsTest{
    @Test
    fun `given a label when I create a Suit from it, then I should have a new suit object with the same label`() {
        val label ="clover"
        assertEquals(label, Suits.valueOf(label).label)
        assertEquals(label, Suits.valueOf(label.lowercase()).label)
    }

    @Test
    fun `given a wrong label when I create a Suit from it, then I should receive an error`() {

        val label ="fake"
        assertFailsWith (Exception::class,"$label is not a valid Suit"){
            Suits.valueOf(label)
        }
    }

//    @Test
//    fun `given a Suit when I serialise it, then I should have the same Suit deserialized`() {
//        val suit: Suits.Suit = Suits.Clover
//        val jsonString = Json.encodeToString(suit);
//        println("suit json: $jsonString")
//        val deserializedSuit = Json.decodeFromString<Suits.Suit>(jsonString)
//        kotlin.test.assertEquals(suit, deserializedSuit)
//    }
}