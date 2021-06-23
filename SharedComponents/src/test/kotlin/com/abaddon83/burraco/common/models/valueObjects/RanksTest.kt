package com.abaddon83.burraco.common.models.valueObjects

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

internal class RanksTest {
    @Test
    fun `given a label when I create a Rank from it, then I should have a new rank object with the same label`() {
        val label ="A"
        assertEquals(label, Ranks.valueOf(label).label)
        assertEquals(label, Ranks.valueOf(label.lowercase()).label)
    }

    @Test
    fun `given a wrong label when I create a Rank from it, then I should receive an error`() {

        val label ="fake"
        assertFailsWith (Exception::class,"$label is not a valid Rank"){
            Ranks.valueOf(label)
        }
    }
}