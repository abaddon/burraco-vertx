package com.abaddon83.burraco.game.adapters.commandController.kafka.handlers

import com.abaddon83.burraco.common.adapter.kafka.KafkaEvent
import com.abaddon83.burraco.common.adapter.kafka.consumer.KafkaEventHandler
import com.abaddon83.burraco.common.externalEvents.dealer.CardDealtToPlayerExternalEvent
import com.abaddon83.burraco.common.helpers.Validated
import com.abaddon83.burraco.common.models.card.Card
import com.abaddon83.burraco.game.ports.CommandControllerPort
import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log
import io.vertx.core.json.Json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

const val CARD_DEALT_TO_PLAYER = "CardDealtToPlayer"

class AddCardPlayerHandlerKafka(private val commandController: CommandControllerPort) :
    KafkaEventHandler(CARD_DEALT_TO_PLAYER) {


    override fun getCoroutineIOScope(): CoroutineScope =
        CoroutineScope(Dispatchers.IO + SupervisorJob())

    override suspend fun handleKafkaEventRequest(event: KafkaEvent): Validated<*, *> {
        log.info("Event ${event.eventName} received")
        val cardDealtToPlayerExternalEventEvent =
            Json.decodeValue(event.eventPayload, CardDealtToPlayerExternalEvent::class.java)
        val card = Card.fromLabel(cardDealtToPlayerExternalEventEvent.cardLabel)
        val gameIdentity = cardDealtToPlayerExternalEventEvent.gameIdentity
        val playerIdentity = cardDealtToPlayerExternalEventEvent.playerIdentity

        return commandController.addCardPlayer(gameIdentity, playerIdentity, card)
    }


}