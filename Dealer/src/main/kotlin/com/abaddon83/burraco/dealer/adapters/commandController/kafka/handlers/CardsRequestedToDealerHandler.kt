package com.abaddon83.burraco.dealer.adapters.commandController.kafka.handlers

import com.abaddon83.burraco.common.adapter.kafka.KafkaEvent
import com.abaddon83.burraco.common.adapter.kafka.consumer.EventHandler
import com.abaddon83.burraco.common.externalEvents.game.CardsRequestedToDealer
import com.abaddon83.burraco.common.helpers.Validated
import com.abaddon83.burraco.dealer.ports.CommandControllerPort
import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log
import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class CardsRequestedToDealerHandler(
    private val commandController: CommandControllerPort,
    vertx: Vertx
) : EventHandler() {

    private val coroutineScope: CoroutineScope = CoroutineScope(vertx.dispatcher())

    suspend fun asyncHandle(event: KafkaEvent?) {
        checkNotNull(event)
        check(event.eventName == CardsRequestedToDealer::class.java.simpleName)
        log.info("Event ${event.eventName} received")
        val cardsRequestedToDealerEvent = Json.decodeValue(event.eventPayload, CardsRequestedToDealer::class.java)
        //val dealerService = DealerService(commandController)
        //val dealerIdentity = DealerIdentity.create()
        val outcome = commandController.cardRequestedToDealer(
            cardsRequestedToDealerEvent.aggregateIdentity,
            cardsRequestedToDealerEvent.players
        )
        when (outcome) {
            is Validated.Invalid -> log.error("The dealer failed to deal cards, {}", outcome.err)
            is Validated.Valid -> log.info("The dealer has dealt cards")
        }

    }

    override fun handle(event: KafkaEvent?) {
        coroutineScope.launch {
            asyncHandle(event)
        }
    }

}

