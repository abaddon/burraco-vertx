package com.abaddon83.burraco.dealer.adapters.commandController.kafka.handlers

import com.abaddon83.burraco.common.externalEvents.KafkaEvent
import com.abaddon83.burraco.common.adapter.kafka.consumer.KafkaEventHandler
import com.abaddon83.burraco.common.externalEvents.game.CardsRequestedToDealerExternalEvent
import com.abaddon83.burraco.common.helpers.Validated
import com.abaddon83.burraco.dealer.ports.CommandControllerPort
import io.github.abaddon.kcqrs.core.helpers.KcqrsLoggerFactory.log
import io.vertx.core.json.Json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

private const val CARDS_REQUESTED_TO_DEALER = "CardsRequestedToDealer"

class CardsRequestedToDealerHandlerKafka(private val commandController: CommandControllerPort) :
    KafkaEventHandler(CARDS_REQUESTED_TO_DEALER) {

    override fun getCoroutineIOScope(): CoroutineScope =
        CoroutineScope(Dispatchers.IO + SupervisorJob())

    override suspend fun handleKafkaEventRequest(event: KafkaEvent): Validated<*, *> {
        log.info("Event ${event.eventName} received")
        val cardsRequestedToDealerEvent = Json.decodeValue(event.eventPayload, CardsRequestedToDealerExternalEvent::class.java)
        return commandController.cardRequestedToDealer(
            cardsRequestedToDealerEvent.aggregateIdentity,
            cardsRequestedToDealerEvent.players
        )
    }

}

