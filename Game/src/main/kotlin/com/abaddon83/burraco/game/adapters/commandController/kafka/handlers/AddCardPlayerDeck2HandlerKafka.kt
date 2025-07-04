package com.abaddon83.burraco.game.adapters.commandController.kafka.handlers

import com.abaddon83.burraco.common.adapter.kafka.KafkaEvent
import com.abaddon83.burraco.common.adapter.kafka.consumer.KafkaEventHandler
import com.abaddon83.burraco.common.externalEvents.dealer.CardDealtToPlayerDeck2ExternalEvent
import com.abaddon83.burraco.common.helpers.Validated
import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.ports.CommandControllerPort
import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log
import io.vertx.core.json.Json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

const val CARD_DEALT_TO_PLAYER_DECK2 = "CardDealtToPlayerDeck2"

class AddCardPlayerDeck2HandlerKafka(private val commandController: CommandControllerPort) :
    KafkaEventHandler(CARD_DEALT_TO_PLAYER_DECK2) {


    override fun getCoroutineIOScope(): CoroutineScope =
        CoroutineScope(Dispatchers.IO + SupervisorJob())

    override suspend fun handleKafkaEventRequest(event: KafkaEvent): Validated<*, *> {
        log.info("Event ${event.eventName} received")
        val cardDealtToPlayerDeck2ExternalEvent =
            Json.decodeValue(event.eventPayload, CardDealtToPlayerDeck2ExternalEvent::class.java)
        val card = Card.fromLabel(cardDealtToPlayerDeck2ExternalEvent.cardLabel)
        val gameIdentity = cardDealtToPlayerDeck2ExternalEvent.gameIdentity

        return commandController.addCardSecondPlayerDeck(gameIdentity, card)
    }


}