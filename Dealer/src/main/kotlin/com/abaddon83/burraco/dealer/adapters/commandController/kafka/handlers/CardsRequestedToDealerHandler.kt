package com.abaddon83.burraco.dealer.adapters.commandController.kafka.handlers

import com.abaddon83.burraco.common.adapter.kafka.KafkaEvent
import com.abaddon83.burraco.common.adapter.kafka.consumer.EventHandler
import com.abaddon83.burraco.common.externalEvents.game.CardsRequestedToDealer
import com.abaddon83.burraco.dealer.models.DealerIdentity
import com.abaddon83.burraco.dealer.ports.CommandControllerPort
import com.abaddon83.burraco.dealer.services.DealerService
import com.abaddon83.burraco.dealer.services.DealerServiceResult
import io.vertx.core.json.Json
import org.slf4j.LoggerFactory

class CardsRequestedToDealerHandler(private val commandController: CommandControllerPort): EventHandler() {
    private val log = LoggerFactory.getLogger(this::class.qualifiedName)
    override suspend fun asyncHandle(event: KafkaEvent?) {
        checkNotNull(event)
        check(event.eventName == CardsRequestedToDealer::class.java.simpleName)
        log.info("Event ${event.eventName} received")
        val cardsRequestedToDealerEvent=Json.decodeValue(event.eventPayload, CardsRequestedToDealer::class.java)
        val dealerService = DealerService(commandController)
        val dealerIdentity=DealerIdentity.create()
        when(val result = dealerService.dealCards(dealerIdentity,cardsRequestedToDealerEvent.aggregateIdentity,cardsRequestedToDealerEvent.players)){
            is DealerServiceResult.Invalid -> log.error("The dealer failed to deal cards",result.err)
            is DealerServiceResult.Valid -> log.info("The dealer has dealt cards")
        }

    }

}

