package com.abaddon83.burraco.game.adapters.commandController.kafka.handlers

import com.abaddon83.burraco.common.adapter.kafka.consumer.KafkaEventHandler
import com.abaddon83.burraco.common.externalEvents.KafkaEvent
import com.abaddon83.burraco.common.externalEvents.dealer.DealingCompletedExternalEvent
import com.abaddon83.burraco.common.helpers.Validated
import com.abaddon83.burraco.game.ports.CommandControllerPort
import io.github.abaddon.kcqrs.core.helpers.KcqrsLoggerFactory.log
import io.vertx.core.json.Json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

const val DEALING_COMPLETED = "DealingCompleted"

class StartGameHandlerKafka(private val commandController: CommandControllerPort) :
    KafkaEventHandler(DEALING_COMPLETED) {

    override fun getCoroutineIOScope(): CoroutineScope =
        CoroutineScope(Dispatchers.IO + SupervisorJob())

    override suspend fun handleKafkaEventRequest(event: KafkaEvent): Validated<*, *> {
        log.info("Event ${event.eventName} received")
        val dealingCompletedExternalEvent =
            Json.decodeValue(event.eventPayload, DealingCompletedExternalEvent::class.java)
        val gameIdentity = dealingCompletedExternalEvent.gameIdentity

        return commandController.startGame(gameIdentity)
    }
}
