package com.abaddon83.burraco.e2e.steps

import com.abaddon83.burraco.e2e.support.HttpClient
import com.abaddon83.burraco.e2e.support.TestContext
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat

/**
 * Step definitions for Player-related scenarios.
 */
class PlayerStepDefinitions {
    private val context = TestContext.get()

    @Given("a new player request for the game created with user equals {string}")
    fun aNewPlayerRequestForTheGameCreatedWithUser(user: String) {
        assertThat("Game ID should exist before creating player", context.gameId, notNullValue())
        println("📝 Preparing to create player with user: $user for game: ${context.gameId}")
        context.lastPlayerResponse = mapOf("user" to user, "gameId" to context.gameId!!)
    }

    @When("the new player request is processed")
    fun theNewPlayerRequestIsProcessed() {
        val user = context.lastPlayerResponse?.get("user") as? String
        val gameId = context.gameId

        assertThat("User should be set", user, notNullValue())
        assertThat("Game ID should be set", gameId, notNullValue())

        println("👤 Creating player with user: $user for game: $gameId...")

        // Create player in Player service
        // Note: Player service publishes PlayerCreated event to Kafka
        // Game service listens to this event and automatically adds the player
        val playerResponse = HttpClient.createPlayer(gameId!!, user!!)
        context.lastResponse = playerResponse

        if (playerResponse.statusCode() == 200) {
            val playerData = playerResponse.body().jsonPath().getMap<String, Any>("")
            context.lastPlayerResponse = playerData
            context.playerId = playerData["playerId"] as String?
            println("✅ Player created with ID: ${context.playerId}")

            // Wait for Kafka event to be processed by Game service
            println("⏳ Waiting for Kafka event to be processed...")
            Thread.sleep(1000) // 1 second delay for event processing
            println("✅ Player should be added to game via Kafka event")
        } else {
            println("❌ Failed to create player: ${playerResponse.statusCode()}")
        }
    }

    @Then("a new player is associated to the game")
    fun aNewPlayerIsAssociatedToTheGame() {
        assertThat("Response status should be 200", context.lastResponse?.statusCode(), equalTo(200))
        assertThat("Player ID should not be null", context.playerId, notNullValue())
        assertThat("Player game ID should match", context.lastPlayerResponse?.get("gameId"), equalTo(context.gameId))
        assertThat("Player user should be set", context.lastPlayerResponse?.get("user"), notNullValue())
        assertThat("Player status should be DRAFT", context.lastPlayerResponse?.get("status"), equalTo("DRAFT"))

        println("✅ Verified: Player ${context.playerId} is associated to game ${context.gameId}")
    }

    @Then("the new player is not associated to the game because the game is full")
    fun theNewPlayerIsNotAssociatedToTheGameBecauseTheGameIsFull() {
        assertThat("Response status should be 400 (Bad Request)", context.lastResponse?.statusCode(), equalTo(400))
        println("✅ Verified: Player association failed as expected - game is full")
    }
}
