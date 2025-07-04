package com.abaddon83.burraco.game.adapters.commandController.kafka.handlers

import com.abaddon83.burraco.common.adapter.kafka.KafkaEvent
import com.abaddon83.burraco.common.adapter.kafka.consumer.KafkaEventHandler
import com.abaddon83.burraco.common.externalEvents.player.PlayerCreated
import com.abaddon83.burraco.common.helpers.Validated
import com.abaddon83.burraco.game.ports.CommandControllerPort
import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log
import io.vertx.core.json.Json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

const val PLAYER_CREATED = "PlayerCreated"

class AddPlayerHandlerKafka(private val commandController: CommandControllerPort) :
    KafkaEventHandler(PLAYER_CREATED) {

    override fun getCoroutineIOScope(): CoroutineScope =
        CoroutineScope(Dispatchers.IO + SupervisorJob())

    override suspend fun handleKafkaEventRequest(event: KafkaEvent): Validated<*, *> {
        log.info("Event ${event.eventName} received")
        val playerCreatedEvent = Json.decodeValue(event.eventPayload, PlayerCreated::class.java)
        val gameIdentity = playerCreatedEvent.gameIdentity
        val playerIdentity = playerCreatedEvent.aggregateIdentity

        return commandController.addPlayer(gameIdentity, playerIdentity)
    }
}