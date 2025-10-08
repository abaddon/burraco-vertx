package com.abaddon83.burraco.player.adapter.commandController.kafka.handlers

import com.abaddon83.burraco.common.externalEvents.KafkaEvent
import com.abaddon83.burraco.common.adapter.kafka.consumer.KafkaEventHandler
import com.abaddon83.burraco.common.externalEvents.dealer.CardDealtToPlayerExternalEvent
import com.abaddon83.burraco.common.helpers.Validated
import com.abaddon83.burraco.common.models.card.Card
import com.abaddon83.burraco.player.port.CommandControllerPort
import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log
import io.vertx.core.json.Json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

const val CARD_DEALT_TO_PLAYER = "CardDealtToPlayer"

class AddCardToPlayerHandlerKafka(private val commandController: CommandControllerPort) :
    KafkaEventHandler(CARD_DEALT_TO_PLAYER) {

    override fun getCoroutineIOScope(): CoroutineScope =
        CoroutineScope(Dispatchers.IO + SupervisorJob())

    override suspend fun handleKafkaEventRequest(event: KafkaEvent): Validated<*, *> {
        log.info("Event ${event.eventName} received")
        val cardDealtToPlayerExternalEvent =
            Json.decodeValue(event.eventPayload, CardDealtToPlayerExternalEvent::class.java)
        val card = Card.fromLabel(cardDealtToPlayerExternalEvent.cardLabel)
        val gameIdentity = cardDealtToPlayerExternalEvent.gameIdentity
        val playerIdentity = cardDealtToPlayerExternalEvent.playerIdentity

        return commandController.addCardToPlayer(gameIdentity, playerIdentity, card)
    }
}
