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
 * Step definitions for Game Start scenarios.
 */
class GameStartStepDefinitions {
    private val context = TestContext.get()

    @Given("an existing game with {int} players")
    fun anExistingGameWithPlayers(playerCount: Int) {
        println("🎮 Creating a new game with $playerCount players...")

        // Create a new game
        val gameResponse = HttpClient.createGame()
        assertThat("Game creation should succeed", gameResponse.statusCode(), equalTo(200))

        val gameData = gameResponse.body().jsonPath().getMap<String, Any>("")
        context.gameId = gameData["gameId"] as String?
        context.lastGameResponse = gameData

        println("✅ Game created with ID: ${context.gameId}")

        // Add players to the game
        for (i in 1..playerCount) {
            val user = "user$i"
            println("👤 Adding player $i with user: $user...")

            // Create player in Player service
            val playerResponse = HttpClient.createPlayer(context.gameId!!, user)
            assertThat("Player creation should succeed", playerResponse.statusCode(), equalTo(200))

            val playerData = playerResponse.body().jsonPath().getMap<String, Any>("")
            val playerId = playerData["playerId"] as String?

            // Associate player with game in Game service
            val addPlayerResponse = HttpClient.addPlayerToGame(context.gameId!!, playerId!!)
            assertThat("Player association should succeed", addPlayerResponse.statusCode(), equalTo(200))

            context.lastGameResponse = addPlayerResponse.body().jsonPath().getMap<String, Any>("")

            // Store the first player ID for requestDealCards
            if (i == 1) {
                context.playerId = playerId
            }

            println("✅ Player $i added successfully")
        }

        println("✅ Game setup complete with $playerCount players")
    }

    @When("the game is started")
    fun theGameIsStarted() {
        println("🎲 Starting the game...")

        // First, request deal cards (this initializes the game)
        println("📋 Requesting deal cards for player ${context.playerId}...")
        val dealCardsResponse = HttpClient.requestDealCards(context.gameId!!, context.playerId!!)

        if (dealCardsResponse.statusCode() == 200) {
            context.lastGameResponse = dealCardsResponse.body().jsonPath().getMap<String, Any>("")
            println("✅ Deal cards requested successfully")
        } else {
            println("⚠️ Deal cards request returned: ${dealCardsResponse.statusCode()}")
            println("Response body: ${dealCardsResponse.body().asString()}")
        }

        context.lastResponse = dealCardsResponse
    }

    @Then("the game status is {string}")
    fun theGameStatusIs(expectedStatus: String) {
        println("🔍 Verifying game status is $expectedStatus...")

        val gameResponse = context.lastGameResponse
        assertThat("Game response should not be null", gameResponse, notNullValue())

        val actualStatus = gameResponse?.get("status") as? String
        assertThat("Game status should be $expectedStatus", actualStatus, equalTo(expectedStatus))

        println("✅ Verified: Game status is $actualStatus")
    }
}
