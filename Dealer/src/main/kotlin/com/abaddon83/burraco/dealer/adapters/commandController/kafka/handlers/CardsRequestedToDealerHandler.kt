package com.abaddon83.burraco.dealer.adapters.commandController.kafka.handlers

import com.abaddon83.burraco.common.adapter.kafka.KafkaEvent
import com.abaddon83.burraco.common.adapter.kafka.consumer.EventHandler
import com.abaddon83.burraco.common.externalEvents.game.CardsRequestedToDealer
import com.abaddon83.burraco.dealer.models.DealerIdentity
import com.abaddon83.burraco.dealer.ports.CommandControllerPort
import io.vertx.core.json.Json

class CardsRequestedToDealerHandler(private val commandController: CommandControllerPort): EventHandler() {
    override suspend fun asyncHandle(event: KafkaEvent?) {
        checkNotNull(event)
        check(event.eventName == CardsRequestedToDealer::class.java.simpleName)
        val cardsRequestedToDealer=Json.decodeValue(event.eventPayload, CardsRequestedToDealer::class.java)

        commandController.createDeck(DealerIdentity.create(), cardsRequestedToDealer.aggregateIdentity, cardsRequestedToDealer.players )
    }
}

