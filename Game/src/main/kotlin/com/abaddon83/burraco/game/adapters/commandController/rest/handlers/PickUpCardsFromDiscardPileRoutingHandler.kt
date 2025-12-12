package com.abaddon83.burraco.game.adapters.commandController.rest.handlers

import com.abaddon83.burraco.common.helpers.Validated
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.card.Card
import com.abaddon83.burraco.common.models.event.game.CardsPickedFromDiscardPile
import com.abaddon83.burraco.game.DomainError
import com.abaddon83.burraco.game.DomainResult
import com.abaddon83.burraco.game.adapters.commandController.rest.models.ErrorMsgModule
import com.abaddon83.burraco.game.ports.CommandControllerPort
import io.github.abaddon.kcqrs.core.helpers.KcqrsLoggerFactory.log
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.Json
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.validation.RequestParameters
import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.kotlin.core.json.get

class PickUpCardsFromDiscardPileRoutingHandler(private val controllerAdapter: CommandControllerPort) : CoroutineRoutingHandler() {

    override suspend fun coroutineHandle(routingContext: RoutingContext) {
        try {
            val params: RequestParameters = routingContext.get(ValidationHandler.REQUEST_CONTEXT_KEY)
            val bodyRequest: JsonObject = params.body().jsonObject
            log.debug("PickUpCardsFromDiscardPileRoutingHandler request body: ${bodyRequest.encode()}")

            val gameIdentity = GameIdentity(params.pathParameter("gameId").string)
            val playerIdentity = PlayerIdentity(bodyRequest.get<String>("playerId"))

            when (val outcome = controllerAdapter.pickUpCardsFromDiscardPile(gameIdentity, playerIdentity)) {
                is Validated.Valid -> handleSuccess(routingContext, outcome.value)
                is Validated.Invalid -> handleError(routingContext, outcome.err)
            }
        } catch (e: IllegalArgumentException) {
            badRequest(routingContext, "Invalid request: ${e.message}")
        } catch (e: Exception) {
            log.error("Unexpected error in PickUpCardsFromDiscardPileRoutingHandler", e)
            internalServerError(routingContext, "Internal server error")
        }
    }

    private fun handleSuccess(routingContext: RoutingContext, domainResult: DomainResult) {
        val cardsPickedEvent = domainResult.events.filterIsInstance<CardsPickedFromDiscardPile>().firstOrNull()

        if (cardsPickedEvent == null) {
            log.error("CardsPickedFromDiscardPile event not found in domain result")
            internalServerError(routingContext, "Cards pick up succeeded but cards information not found")
            return
        }

        val cards = cardsPickedEvent.cards
        val cardsResponse = createCardsResponse(cards)

        routingContext
            .response()
            .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .setStatusCode(200)
            .end(cardsResponse.encode())
    }

    private fun handleError(routingContext: RoutingContext, domainError: DomainError) {
        val errorMessage = domainError.toMap()["message"]?.toString() ?: "Unknown error"

        when {
            errorMessage.contains("not a player", ignoreCase = true) -> {
                badRequest(routingContext, errorMessage)
            }
            errorMessage.contains("not the turn", ignoreCase = true) -> {
                forbidden(routingContext, errorMessage)
            }
            errorMessage.contains("empty", ignoreCase = true) ||
            errorMessage.contains("Discard pile is empty", ignoreCase = true) -> {
                conflict(routingContext, errorMessage)
            }
            errorMessage.contains("wrong status", ignoreCase = true) ||
            errorMessage.contains("UnsupportedOperationException", ignoreCase = true) -> {
                conflict(routingContext, errorMessage)
            }
            else -> {
                badRequest(routingContext, errorMessage)
            }
        }
    }

    private fun createCardsResponse(cards: List<Card>): JsonObject {
        val cardsArray = JsonArray()
        cards.forEach { card ->
            cardsArray.add(
                JsonObject()
                    .put("cardId", card.label)
                    .put("suit", card.suit.label)
                    .put("rank", card.rank.label)
                    .put("cardType", if (card.suit.label == "Jolly") "JOKER" else "STANDARD")
            )
        }
        return JsonObject().put("cards", cardsArray)
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
