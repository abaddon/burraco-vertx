package com.abaddon83.burraco.e2e.steps

import com.abaddon83.burraco.e2e.support.HttpClient
import com.abaddon83.burraco.e2e.support.TestContext
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat

/**
 * Step definitions for Pick Up Cards from Discard Pile scenarios.
 */
class PickUpCardsFromDiscardPileStepDefinitions {
    private val context = TestContext.get()

    @When("the current player picks up cards from the discard pile")
    fun theCurrentPlayerPicksUpCardsFromTheDiscardPile() {
        println("🃏 Current player picking up cards from the discard pile...")

        assertThat("Game ID should exist", context.gameId, notNullValue())
        assertThat("Player IDs should not be empty", context.playerIds.isNotEmpty(), equalTo(true))

        // Use the first player (who should be the current turn player)
        val currentPlayerId = context.playerIds[0]
        println("📋 Player $currentPlayerId attempting to pick up cards from discard pile...")

        val pickUpResponse = HttpClient.pickUpCardsFromDiscardPile(context.gameId!!, currentPlayerId)
        context.lastResponse = pickUpResponse

        println("✅ Pick up cards request completed with status: ${pickUpResponse.statusCode()}")
        if (pickUpResponse.statusCode() != 200) {
            println("⚠️ Response body: ${pickUpResponse.body().asString()}")
        }
    }

    @When("the wrong player tries to pick up cards from the discard pile")
    fun theWrongPlayerTriesToPickUpCardsFromTheDiscardPile() {
        println("🃏 Wrong player trying to pick up cards from the discard pile...")

        assertThat("Game ID should exist", context.gameId, notNullValue())
        assertThat("At least 2 players should exist", context.playerIds.size >= 2, equalTo(true))

        // Use the second player (who should NOT be the current turn player)
        val wrongPlayerId = context.playerIds[1]
        println("📋 Player $wrongPlayerId (wrong player) attempting to pick up cards from discard pile...")

        val pickUpResponse = HttpClient.pickUpCardsFromDiscardPile(context.gameId!!, wrongPlayerId)
        context.lastResponse = pickUpResponse

        println("✅ Pick up cards request completed with status: ${pickUpResponse.statusCode()}")
        if (pickUpResponse.statusCode() != 200) {
            println("⚠️ Response body: ${pickUpResponse.body().asString()}")
        }
    }

    @When("the discard pile is empty")
    fun theDiscardPileIsEmpty() {
        println("🗑️ Discard pile is empty...")

        // Note: In the initial game state after cards are dealt, the discard pile has 1 card
        // To truly test an empty discard pile scenario, we would need to:
        // 1. Pick up the discard pile card(s)
        // 2. Or have a test API endpoint to clear the discard pile
        // For now, this is a placeholder for the test scenario

        println("⚠️ Warning: This step is a placeholder. Empty discard pile setup would require additional game actions.")
        println("✅ Assuming discard pile is empty (placeholder)")
    }

    @Then("the pick up cards request is successful")
    fun thePickUpCardsRequestIsSuccessful() {
        println("🔍 Verifying pick up cards request was successful...")

        assertThat("Response should not be null", context.lastResponse, notNullValue())
        val statusCode = context.lastResponse!!.statusCode()

        assertThat("Pick up cards request should return 200 OK", statusCode, equalTo(200))

        println("✅ Verified: Pick up cards request successful (200 OK)")
    }

    @Then("the response contains a list of cards")
    fun theResponseContainsAListOfCards() {
        println("🔍 Verifying response contains a list of cards...")

        assertThat("Response should not be null", context.lastResponse, notNullValue())

        val responseBody = context.lastResponse!!.body().jsonPath()

        val cards = responseBody.getList<Map<String, Any>>("cards")

        assertThat("Cards list should not be null", cards, notNullValue())
        assertThat("Cards list should not be empty", cards.isNotEmpty(), equalTo(true))

        println("✅ Verified: Response contains a list of ${cards.size} cards")

        // Verify first card structure
        if (cards.isNotEmpty()) {
            val firstCard = cards[0]
            assertThat("Card should have cardId", firstCard["cardId"], notNullValue())
            assertThat("Card should have suit", firstCard["suit"], notNullValue())
            assertThat("Card should have rank", firstCard["rank"], notNullValue())
            assertThat("Card should have cardType", firstCard["cardType"], notNullValue())

            println("   - First Card ID: ${firstCard["cardId"]}")
            println("   - First Card Suit: ${firstCard["suit"]}")
            println("   - First Card Rank: ${firstCard["rank"]}")
            println("   - First Card Type: ${firstCard["cardType"]}")
        }
    }

    @Then("the pick up cards request fails with forbidden status")
    fun thePickUpCardsRequestFailsWithForbiddenStatus() {
        println("🔍 Verifying pick up cards request failed with 403 Forbidden...")

        assertThat("Response should not be null", context.lastResponse, notNullValue())
        val statusCode = context.lastResponse!!.statusCode()

        assertThat("Pick up cards request should return 403 Forbidden", statusCode, equalTo(403))

        println("✅ Verified: Pick up cards request failed with 403 Forbidden")
    }

    @Then("the pick up cards request fails with conflict status")
    fun thePickUpCardsRequestFailsWithConflictStatus() {
        println("🔍 Verifying pick up cards request failed with 409 Conflict...")

        assertThat("Response should not be null", context.lastResponse, notNullValue())
        val statusCode = context.lastResponse!!.statusCode()

        assertThat("Pick up cards request should return 409 Conflict", statusCode, equalTo(409))

        println("✅ Verified: Pick up cards request failed with 409 Conflict")
    }
}
