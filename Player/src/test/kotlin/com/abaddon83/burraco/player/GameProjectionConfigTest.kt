package com.abaddon83.burraco.player

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class GameProjectionConfigTest {

    @Test
    fun `Given GameProjectionConfig when getEventStoreDBConfig called then returns correct config`() {
        val connectionString = "kurrentdb://localhost:2113?tls=false"
        val config = GameProjectionConfig(
            gameStreamName = "game_stream",
            eventStoreConnectionString = connectionString,
            startFromBeginning = true
        )

        val eventStoreConfig = config.getEventStoreDBConfig()

        assertNotNull(eventStoreConfig)
        assertEquals(connectionString, config.eventStoreConnectionString)
    }

    @Test
    fun `Given GameProjectionConfig when getSubscriptionFilterConfig called then returns null`() {
        val config = GameProjectionConfig(
            gameStreamName = "game_stream",
            eventStoreConnectionString = "kurrentdb://localhost:2113",
            startFromBeginning = true
        )

        val filterConfig = config.getSubscriptionFilterConfig()

        assertNull(filterConfig)
    }

    @Test
    fun `Given empty factory method when called then returns correct empty config`() {
        val emptyConfig = GameProjectionConfig.empty()

        assertEquals("", emptyConfig.gameStreamName)
        assertEquals("", emptyConfig.eventStoreConnectionString)
        assertTrue(emptyConfig.startFromBeginning)
    }

    @Test
    fun `Given GameProjectionConfig with default startFromBeginning when created then defaults to true`() {
        val config = GameProjectionConfig(
            gameStreamName = "game_stream",
            eventStoreConnectionString = "kurrentdb://localhost:2113"
        )

        assertTrue(config.startFromBeginning)
    }

    @Test
    fun `Given GameProjectionConfig with startFromBeginning true when accessed then returns true`() {
        val config = GameProjectionConfig(
            gameStreamName = "game_stream",
            eventStoreConnectionString = "kurrentdb://localhost:2113",
            startFromBeginning = true
        )

        assertTrue(config.startFromBeginning)
    }

    @Test
    fun `Given GameProjectionConfig with startFromBeginning false when accessed then returns false`() {
        val config = GameProjectionConfig(
            gameStreamName = "game_stream",
            eventStoreConnectionString = "kurrentdb://localhost:2113",
            startFromBeginning = false
        )

        assertFalse(config.startFromBeginning)
    }
}