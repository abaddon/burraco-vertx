package com.abaddon83.burraco.game.adapters.commandController.kafka.handlers

import com.abaddon83.burraco.common.externalEvents.KafkaEvent
import com.abaddon83.burraco.common.adapter.kafka.consumer.KafkaEventHandler
import com.abaddon83.burraco.common.externalEvents.dealer.CardDealtToDeckExternalEvent
import com.abaddon83.burraco.common.helpers.Validated
import com.abaddon83.burraco.common.models.card.Card
import com.abaddon83.burraco.game.ports.CommandControllerPort
import io.github.abaddon.kcqrs.core.helpers.KcqrsLoggerFactory.log
import io.vertx.core.json.Json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

const val CARD_DEALT_TO_DECK = "CardDealtToDeck"

class AddCardDeckHandlerKafka(private val commandController: CommandControllerPort) :
    KafkaEventHandler(CARD_DEALT_TO_DECK) {

    override fun getCoroutineIOScope(): CoroutineScope =
        CoroutineScope(Dispatchers.IO + SupervisorJob())

    override suspend fun handleKafkaEventRequest(event: KafkaEvent): Validated<*, *> {
        log.info("Event ${event.eventName} received")
        val cardDealtToDeckExternalEventEvent =
            Json.decodeValue(event.eventPayload, CardDealtToDeckExternalEvent::class.java)
        val card = Card.fromLabel(cardDealtToDeckExternalEventEvent.cardLabel)
        val gameIdentity = cardDealtToDeckExternalEventEvent.gameIdentity

        return commandController.addCardDeck(gameIdentity, card)
    }


}