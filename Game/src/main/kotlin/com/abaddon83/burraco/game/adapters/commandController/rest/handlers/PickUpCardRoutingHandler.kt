package com.abaddon83.burraco.game.adapters.commandController.rest.handlers

import com.abaddon83.burraco.common.helpers.Validated
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.card.Card
import com.abaddon83.burraco.common.models.event.game.CardPickedFromDeck
import com.abaddon83.burraco.game.DomainError
import com.abaddon83.burraco.game.DomainResult
import com.abaddon83.burraco.game.adapters.commandController.rest.models.ErrorMsgModule
import com.abaddon83.burraco.game.ports.CommandControllerPort
import io.github.abaddon.kcqrs.core.helpers.KcqrsLoggerFactory.log
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.validation.RequestParameters
import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.kotlin.core.json.get

class PickUpCardRoutingHandler(private val controllerAdapter: CommandControllerPort) : CoroutineRoutingHandler() {

    override suspend fun coroutineHandle(routingContext: RoutingContext) {
        try {
            val params: RequestParameters = routingContext.get(ValidationHandler.REQUEST_CONTEXT_KEY)
            val bodyRequest: JsonObject = params.body().jsonObject
            log.debug("PickUpCardRoutingHandler request body: ${bodyRequest.encode()}")

            val gameIdentity = GameIdentity(params.pathParameter("gameId").string)
            val playerIdentity = PlayerIdentity(bodyRequest.get<String>("playerId"))

            when (val outcome = controllerAdapter.pickUpCard(gameIdentity, playerIdentity)) {
                is Validated.Valid -> handleSuccess(routingContext, outcome.value)
                is Validated.Invalid -> handleError(routingContext, outcome.err)
            }
        } catch (e: IllegalArgumentException) {
            // Invalid player ID or malformed request
            badRequest(routingContext, "Invalid request: ${e.message}")
        } catch (e: Exception) {
            log.error("Unexpected error in PickUpCardRoutingHandler", e)
            internalServerError(routingContext, "Internal server error")
        }
    }

    private fun handleSuccess(routingContext: RoutingContext, domainResult: DomainResult) {
        // Extract the card from the CardPickedFromDeck event
        val cardPickedEvent = domainResult.events.filterIsInstance<CardPickedFromDeck>().firstOrNull()

        if (cardPickedEvent == null) {
            log.error("CardPickedFromDeck event not found in domain result")
            internalServerError(routingContext, "Card pick up succeeded but card information not found")
            return
        }

        val card = cardPickedEvent.card
        val cardResponse = createCardResponse(card)

        routingContext
            .response()
            .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .setStatusCode(200)
            .end(cardResponse.encode())
    }

    private fun handleError(routingContext: RoutingContext, domainError: DomainError) {
        val errorMessage = domainError.toMap()["message"]?.toString() ?: "Unknown error"

        // Determine appropriate HTTP status code based on error message
        when {
            errorMessage.contains("not a player", ignoreCase = true) -> {
                // Invalid player ID
                badRequest(routingContext, errorMessage)
            }
            errorMessage.contains("not the turn", ignoreCase = true) -> {
                // Not the player's turn
                forbidden(routingContext, errorMessage)
            }
            errorMessage.contains("empty", ignoreCase = true) ||
            errorMessage.contains("Deck is empty", ignoreCase = true) -> {
                // Deck is empty
                conflict(routingContext, errorMessage)
            }
            errorMessage.contains("wrong status", ignoreCase = true) ||
            errorMessage.contains("UnsupportedOperationException", ignoreCase = true) -> {
                // Game in wrong state
                conflict(routingContext, errorMessage)
            }
            else -> {
                // Default to bad request for other errors
                badRequest(routingContext, errorMessage)
            }
        }
    }

    private fun createCardResponse(card: Card): JsonObject {
        return JsonObject()
            .put("cardId", card.label)
            .put("suit", card.suit.label)
            .put("rank", card.rank.label)
            .put("cardType", if (card.suit.label == "Jolly") "JOKER" else "STANDARD")
    }

    private fun badRequest(routingContext: RoutingContext, message: String) {
        sendErrorResponse(routingContext, 400, message)
    }

    private fun forbidden(routingContext: RoutingContext, message: String) {
        sendErrorResponse(routingContext, 403, message)
    }

    private fun conflict(routingContext: RoutingContext, message: String) {
        sendErrorResponse(routingContext, 409, message)
    }

    private fun internalServerError(routingContext: RoutingContext, message: String) {
        sendErrorResponse(routingContext, 500, message)
    }

    private fun sendErrorResponse(routingContext: RoutingContext, statusCode: Int, message: String) {
        routingContext.response()
            .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .setStatusCode(statusCode)
            .end(Json.encodePrettily(ErrorMsgModule(code = statusCode, errorMessages = listOf(mapOf("message" to message)))))
    }
}
