package com.abaddon83.burraco.game.adapters.commandController.kafka.handlers

import com.abaddon83.burraco.common.adapter.kafka.KafkaEvent
import com.abaddon83.burraco.common.adapter.kafka.consumer.EventHandler
import com.abaddon83.burraco.common.externalEvents.dealer.CardGivenToDeck
import com.abaddon83.burraco.common.helpers.Validated
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.ports.CommandControllerPort
import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log
import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AddCardDeckHandler(private val controllerAdapter: CommandControllerPort, vertx: Vertx) : EventHandler(vertx) {

    private val coroutineScope: CoroutineScope = CoroutineScope(vertx.dispatcher())

    suspend fun asyncHandle(event: KafkaEvent?) {
        checkNotNull(event)
        check(event.eventName == CardGivenToDeck::class.java.simpleName)
        log.info("Event ${event.eventName} received")
        val cardGivenToDeckEvent = Json.decodeValue(event.eventPayload, CardGivenToDeck::class.java)
        val card = Card.fromLabel(cardGivenToDeckEvent.cardLabel)
        val gameIdentity = cardGivenToDeckEvent.gameIdentity

        when (val outcome = controllerAdapter.addCardDeck(gameIdentity, card)) {
            is Validated.Valid -> log.info("The dealer has dealt cards")
            is Validated.Invalid -> log.error("The dealer failed to deal cards {}", outcome.err)
        }

    }

    override fun handle(event: KafkaEvent?) {
        coroutineScope.launch {
            asyncHandle(event)
            val gameIdentity = GameIdentity.empty();

            val card = Card.fromLabel("")
            controllerAdapter.addCardDeck(gameIdentity, card)
        }

    }
}