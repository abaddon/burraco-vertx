package com.abaddon83.burraco.dealer.adapters.commandController.kafka.handlers

import com.abaddon83.burraco.common.adapter.kafka.KafkaEvent
import com.abaddon83.burraco.common.adapter.kafka.consumer.EventHandler
import com.abaddon83.burraco.common.externalEvents.game.CardsRequestedToDealer
import com.abaddon83.burraco.common.helpers.Validated
import com.abaddon83.burraco.dealer.ports.CommandControllerPort
import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log
import io.vertx.core.json.Json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class CardsRequestedToDealerHandler(
    private val commandController: CommandControllerPort
) : EventHandler() {

    private val coroutineIOScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun handle(event: KafkaEvent?) {
        val job = coroutineIOScope.launch {
            handleCardsRequested(event)
        }
        // Wait for the coroutine to complete

        runBlocking { job.join() }
    }

    private suspend fun handleCardsRequested(event: KafkaEvent?) {
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
        logOutcome(outcome)

    }

    private fun logOutcome(outcome: Validated<*, *>) {
        when (outcome) {
            is Validated.Invalid -> log.error("Failed to deal cards: {}", outcome.err)
            is Validated.Valid -> log.info("Cards dealt successfully")
        }
    }

}

