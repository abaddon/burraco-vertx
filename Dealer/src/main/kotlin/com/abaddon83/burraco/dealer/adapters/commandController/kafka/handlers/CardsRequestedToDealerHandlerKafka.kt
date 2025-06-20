package com.abaddon83.burraco.dealer.adapters.commandController.kafka.handlers

import com.abaddon83.burraco.common.adapter.kafka.KafkaEvent
import com.abaddon83.burraco.common.adapter.kafka.consumer.KafkaEventHandler
import com.abaddon83.burraco.common.externalEvents.game.CardsRequestedToDealer
import com.abaddon83.burraco.common.helpers.Validated
import com.abaddon83.burraco.dealer.ports.CommandControllerPort
import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log
import io.vertx.core.json.Json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class CardsRequestedToDealerHandlerKafka(
    private val commandController: CommandControllerPort
) : KafkaEventHandler() {

    override fun getCoroutineIOScope(): CoroutineScope =
        CoroutineScope(Dispatchers.IO + SupervisorJob())

    override suspend fun handleKafkaEventRequest(event: KafkaEvent?): Validated<*, *> {
        checkNotNull(event)
        check(event.eventName == CardsRequestedToDealer::class.java.simpleName)
        log.info("Event ${event.eventName} received")
        val cardsRequestedToDealerEvent = Json.decodeValue(event.eventPayload, CardsRequestedToDealer::class.java)
        return commandController.cardRequestedToDealer(
            cardsRequestedToDealerEvent.aggregateIdentity,
            cardsRequestedToDealerEvent.players
        )
    }

}

