package com.abaddon83.burraco.game.helpers

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

internal class StringFunctionsKtTest{

    @Test
    fun `Given a valid UUID when tested then result is true`() {
        val expected = true
        val value = UUID.randomUUID().toString()
        val actual = value.validUUID()
        assertEquals(expected, actual)
    }

    @Test
    fun `Given a not valid UUID when tested then result is false`() {
        val expected = false
        val wrongUUID = "336cf48c-9bdb-4901-a8d7-215b1e44075"
        val actual = wrongUUID.validUUID()
        assertEquals(expected, actual)
    }
}