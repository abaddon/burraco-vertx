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
 * Step definitions for Game-related scenarios.
 */
class GameStepDefinitions {
    private val context = TestContext.get()

    @Given("a valid new game request")
    fun aValidNewGameRequest() {
        // No setup needed - the request is implicit (GET /game/burraco)
        println("📝 Preparing to create a new game")
    }

    @When("the new game request is processed")
    fun theNewGameRequestIsProcessed() {
        println("🎮 Creating new game...")
        val response = HttpClient.createGame()
        context.lastResponse = response

        // Store response for verification
        if (response.statusCode() == 200) {
            val gameResponse = response.body().jsonPath().getMap<String, Any>("")
            context.lastGameResponse = gameResponse
            context.gameId = gameResponse["gameId"] as String?
            println("✅ Game created with ID: ${context.gameId}")
        }
    }

    @Then("a new game is created")
    fun aNewGameIsCreated() {
        assertThat("Response status should be 200", context.lastResponse?.statusCode(), equalTo(200))
        assertThat("Game ID should not be null", context.gameId, notNullValue())
        assertThat("Game type should be BURRACO", context.lastGameResponse?.get("type"), equalTo("BURRACO"))
        assertThat("Game status should be DRAFT", context.lastGameResponse?.get("status"), equalTo("DRAFT"))

        println("✅ Verified: Game created successfully with status DRAFT")
    }

    @Then("the game has a player associated")
    fun theGameHasAPlayerAssociated() {
        // Re-fetch the game to verify player association
        println("🔍 Verifying player association in game...")

        // We'll need to add a GET endpoint for games, or verify through the add player response
        // For now, we can verify from the addPlayer response stored in context
        val gameResponse = context.lastGameResponse
        assertThat("Game response should not be null", gameResponse, notNullValue())

        val players = gameResponse?.get("player") as? List<*>
        assertThat("Game should have players", players, notNullValue())
        assertThat("Game should have at least 1 player", players?.isNotEmpty(), equalTo(true))
        assertThat("Player should be in the game", players?.contains(context.playerId), equalTo(true))

        println("✅ Verified: Player ${context.playerId} is associated with game ${context.gameId}")
    }
}
