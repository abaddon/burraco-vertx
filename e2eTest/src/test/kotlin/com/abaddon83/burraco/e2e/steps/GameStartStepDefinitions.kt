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
            // Note: Player service publishes PlayerCreated event to Kafka
            // Game service listens to this event and automatically adds the player
            val playerResponse = HttpClient.createPlayer(context.gameId!!, user)
            assertThat("Player creation should succeed", playerResponse.statusCode(), equalTo(200))

            val playerData = playerResponse.body().jsonPath().getMap<String, Any>("")
            val playerId = playerData["playerId"] as String?

            // Store all player IDs
            context.playerIds.add(playerId!!)

            // Store the first player ID for requestDealCards
            //if (i == 1) {
                context.playerId = playerId
            //}

            println("✅ Player $i created with ID: $playerId")
            Thread.sleep(2000) // 2 second delay for event processing
        }

        // Wait for Kafka events to be processed by Game service
        println("⏳ Waiting for Kafka events to be processed...")
        Thread.sleep(2000) // 2 second delay for event processing
        println("✅ All players should be added to game via Kafka events")
        println("✅ Game setup complete with $playerCount players")
    }

    @When("cards are requested")
    fun cardsAreRequested() {
        println("🎲 Requesting cards to be dealt...")

        // Use the last player ID created (most recent player)
        val lastPlayerId = context.playerId
        assertThat("At least one player should exist", lastPlayerId, notNullValue())

        println("📋 Requesting deal cards for player $lastPlayerId...")
        val dealCardsResponse = HttpClient.requestDealCards(context.gameId!!, lastPlayerId!!)

        if (dealCardsResponse.statusCode() == 200) {
            context.lastGameResponse = dealCardsResponse.body().jsonPath().getMap<String, Any>("")
            println("✅ Deal cards requested successfully")
            Thread.sleep(5000) // 2 second delay for event processing
        } else {
            println("⚠️ Deal cards request returned: ${dealCardsResponse.statusCode()}")
            println("Response body: ${dealCardsResponse.body().asString()}")
            assertThat("Deal cards request failed", false, equalTo(true))
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

    @Then("each player has {int} cards in hand")
    fun eachPlayerHasCardsInHand(expectedCardCount: Int) {
        println("🔍 Verifying each player has $expectedCardCount cards in hand...")

        assertThat("Game ID should exist", context.gameId, notNullValue())
        assertThat("Player IDs should not be empty", context.playerIds.isNotEmpty(), equalTo(true))

        for ((index, playerId) in context.playerIds.withIndex()) {
            println("🃏 Checking player ${index + 1} (ID: $playerId)...")

            val playerViewResponse = HttpClient.getPlayerView(playerId, context.gameId!!)

            if (playerViewResponse.statusCode() != 200) {
                println("⚠️ Failed to get player view: ${playerViewResponse.statusCode()}")
                println("Response body: ${playerViewResponse.body().asString()}")
            }

            assertThat("Player view request should succeed for player $playerId",
                playerViewResponse.statusCode(), equalTo(200))

            val playerView = playerViewResponse.body().jsonPath().getMap<String, Any>("")
            val cards = playerView["cards"] as? List<*>

            assertThat("Player $playerId should have cards", cards, notNullValue())
            assertThat("Player $playerId should have exactly $expectedCardCount cards",
                cards?.size, equalTo(expectedCardCount))

            println("✅ Player ${index + 1} has ${cards?.size} cards")
        }

        println("✅ Verified: All ${context.playerIds.size} players have $expectedCardCount cards in hand")
    }
}
