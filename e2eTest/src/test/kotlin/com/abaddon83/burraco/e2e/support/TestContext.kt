package com.abaddon83.burraco.e2e.support

import io.restassured.response.Response

/**
 * Shared context between Cucumber step definitions.
 * Stores data that needs to be passed between steps.
 */
class TestContext {
    var gameId: String? = null
    var playerId: String? = null
    var playerIds: MutableList<String> = mutableListOf()
    var lastResponse: Response? = null
    var lastGameResponse: Map<String, Any>? = null
    var lastPlayerResponse: Map<String, Any>? = null

    fun reset() {
        gameId = null
        playerId = null
        playerIds.clear()
        lastResponse = null
        lastGameResponse = null
        lastPlayerResponse = null
    }

    companion object {
        private val threadLocalContext = ThreadLocal<TestContext>()

        fun get(): TestContext {
            if (threadLocalContext.get() == null) {
                threadLocalContext.set(TestContext())
            }
            return threadLocalContext.get()
        }

        fun clear() {
            threadLocalContext.get()?.reset()
            threadLocalContext.remove()
        }
    }
}
