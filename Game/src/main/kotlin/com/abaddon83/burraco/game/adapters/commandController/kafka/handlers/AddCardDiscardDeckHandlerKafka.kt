package com.abaddon83.burraco.game.adapters.commandController.kafka.handlers

import com.abaddon83.burraco.common.externalEvents.KafkaEvent
import com.abaddon83.burraco.common.adapter.kafka.consumer.KafkaEventHandler
import com.abaddon83.burraco.common.externalEvents.dealer.CardDealtToDiscardDeckExternalEvent
import com.abaddon83.burraco.common.helpers.Validated
import com.abaddon83.burraco.common.models.card.Card
import com.abaddon83.burraco.game.ports.CommandControllerPort
import io.github.abaddon.kcqrs.core.helpers.KcqrsLoggerFactory.log
import io.vertx.core.json.Json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

const val CARD_DEALT_TO_DISCARD_DECK = "CardDealtToDiscardDeck"

class AddCardDiscardDeckHandlerKafka(private val commandController: CommandControllerPort) :
    KafkaEventHandler(CARD_DEALT_TO_DISCARD_DECK) {


    override fun getCoroutineIOScope(): CoroutineScope =
        CoroutineScope(Dispatchers.IO + SupervisorJob())

    override suspend fun handleKafkaEventRequest(event: KafkaEvent): Validated<*, *> {
        log.info("Event ${event.eventName} received")
        val cardDealtToDiscardDeckExternalEvent =
            Json.decodeValue(event.eventPayload, CardDealtToDiscardDeckExternalEvent::class.java)
        val card = Card.fromLabel(cardDealtToDiscardDeckExternalEvent.cardLabel)
        val gameIdentity = cardDealtToDiscardDeckExternalEvent.gameIdentity

        return commandController.addCardDiscardDeck(gameIdentity, card)
    }


}