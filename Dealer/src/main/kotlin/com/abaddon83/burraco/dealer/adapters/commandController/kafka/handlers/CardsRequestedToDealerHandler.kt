package com.abaddon83.burraco.dealer.adapters.commandController.kafka.handlers

import com.abaddon83.burraco.common.adapter.kafka.KafkaEvent
import com.abaddon83.burraco.common.adapter.kafka.consumer.EventHandler
import com.abaddon83.burraco.common.externalEvents.game.CardsRequestedToDealer
import com.abaddon83.burraco.common.models.DealerIdentity
import com.abaddon83.burraco.dealer.ports.CommandControllerPort
import com.abaddon83.burraco.dealer.services.DealerService
import com.abaddon83.burraco.dealer.services.DealerServiceResult
import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log
import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class CardsRequestedToDealerHandler(
    private val commandController: CommandControllerPort,
    vertx: Vertx
) : EventHandler(vertx) {

    private val coroutineScope: CoroutineScope = CoroutineScope(vertx.dispatcher())

    suspend fun asyncHandle(event: KafkaEvent?) {
        checkNotNull(event)
        check(event.eventName == CardsRequestedToDealer::class.java.simpleName)
        log.info("Event ${event.eventName} received")
        val cardsRequestedToDealerEvent = Json.decodeValue(event.eventPayload, CardsRequestedToDealer::class.java)
        val dealerService = DealerService(commandController)
        val dealerIdentity = DealerIdentity.create()
        when (val result = dealerService.dealCards(
            dealerIdentity,
            cardsRequestedToDealerEvent.aggregateIdentity,
            cardsRequestedToDealerEvent.players
        )) {
            is DealerServiceResult.Invalid -> log.error("The dealer failed to deal cards", result.err)
            is DealerServiceResult.Valid -> log.info("The dealer has dealt cards")
        }

    }

    override fun handle(event: KafkaEvent?) {
        coroutineScope.launch {
            asyncHandle(event)
        }
    }

}

