package com.abaddon83.burraco.e2e.support

import com.abaddon83.burraco.e2e.infrastructure.ServiceEndpoints
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.Response
import io.restassured.specification.RequestSpecification

/**
 * HTTP client wrapper using REST-assured.
 * Provides convenient methods for calling Burraco service APIs.
 */
object HttpClient {

    private fun gameService(): RequestSpecification {
        return RestAssured.given()
            .baseUri(ServiceEndpoints.get().gameServiceUrl)
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
    }

    private fun playerService(): RequestSpecification {
        return RestAssured.given()
            .baseUri(ServiceEndpoints.get().playerServiceUrl)
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
    }

    // Game Service API calls
    fun createGame(): Response {
        return gameService()
            .get("/game/burraco")
    }

    fun addPlayerToGame(gameId: String, playerId: String): Response {
        return gameService()
            .body(mapOf("playerId" to playerId))
            .post("/games/$gameId/player/add")
    }

    fun requestDealCards(gameId: String, playerId: String): Response {
        return gameService()
            .body(mapOf("playerId" to playerId))
            .post("/games/$gameId/requestDealCards")
    }

    fun startGame(gameId: String): Response {
        return gameService()
            .post("/games/$gameId/start")
    }

    fun getGameHealth(): Response {
        return gameService()
            .get("/health")
    }

    // Player Service API calls
    fun createPlayer(gameId: String, user: String): Response {
        return playerService()
            .body(mapOf("gameId" to gameId, "user" to user))
            .post("/player")
    }

    fun getPlayerView(playerId: String, gameId: String): Response {
        return playerService()
            .get("/player/$playerId/game/$gameId/view")
    }

    fun getPlayerHealth(): Response {
        return playerService()
            .get("/health")
    }
}
