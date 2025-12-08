package com.abaddon83.burraco.player.adapter.commandController.kafka

import com.abaddon83.burraco.common.adapter.kafka.consumer.EventRouterHandler
import com.abaddon83.burraco.common.adapter.kafka.consumer.KafkaConsumerVerticle
import com.abaddon83.burraco.player.ServiceConfig
import com.abaddon83.burraco.player.adapter.commandController.kafka.handlers.GameStartedHandlerKafka
import com.abaddon83.burraco.player.port.CommandControllerPort

/**
 * Kafka consumer verticle for handling command-related game events.
 *
 * Consumes events from the game-events topic and processes:
 * - GameStarted: Activates/deactivates players based on turn
 *
 * Note: This is separate from GameViewProjectionKafkaVerticle which handles
 * read model projections. This verticle handles write-side commands.
 */
class KafkaGameConsumerVerticle(
    serviceConfig: ServiceConfig,
    private val commandController: CommandControllerPort
) : KafkaConsumerVerticle(serviceConfig.kafkaGameConsumer) {

    override fun loadHandlers(): EventRouterHandler = EventRouterHandler()
        .addHandler(GameStartedHandlerKafka(commandController))
}
