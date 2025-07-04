package com.abaddon83.burraco.game.adapters.commandController.kafka.handlers

import com.abaddon83.burraco.common.adapter.kafka.KafkaEvent
import com.abaddon83.burraco.common.adapter.kafka.consumer.KafkaEventHandler
import com.abaddon83.burraco.common.externalEvents.dealer.CardDealtToPlayerDeck1ExternalEvent
import com.abaddon83.burraco.common.helpers.Validated
import com.abaddon83.burraco.common.models.card.Card
import com.abaddon83.burraco.game.ports.CommandControllerPort
import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log
import io.vertx.core.json.Json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

const val CARD_DEALT_TO_PLAYER_DECK1 = "CardDealtToPlayerDeck1"

class AddCardPlayerDeck1HandlerKafka(private val commandController: CommandControllerPort) :
    KafkaEventHandler(CARD_DEALT_TO_PLAYER_DECK1) {


    override fun getCoroutineIOScope(): CoroutineScope =
        CoroutineScope(Dispatchers.IO + SupervisorJob())

    override suspend fun handleKafkaEventRequest(event: KafkaEvent): Validated<*, *> {
        log.info("Event ${event.eventName} received")
        val cardDealtToPlayerDeck1ExternalEvent =
            Json.decodeValue(event.eventPayload, CardDealtToPlayerDeck1ExternalEvent::class.java)
        val card = Card.fromLabel(cardDealtToPlayerDeck1ExternalEvent.cardLabel)
        val gameIdentity = cardDealtToPlayerDeck1ExternalEvent.gameIdentity

        return commandController.addCardFirstPlayerDeck(gameIdentity, card)
    }


}