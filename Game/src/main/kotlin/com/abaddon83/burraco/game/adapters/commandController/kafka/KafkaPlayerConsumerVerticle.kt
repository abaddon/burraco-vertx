package com.abaddon83.burraco.game.adapters.commandController.kafka

import com.abaddon83.burraco.common.adapter.kafka.consumer.EventRouterHandler
import com.abaddon83.burraco.common.adapter.kafka.consumer.KafkaConsumerVerticle
import com.abaddon83.burraco.game.ServiceConfig
import com.abaddon83.burraco.game.adapters.commandController.kafka.handlers.AddPlayerHandlerKafka
import com.abaddon83.burraco.game.adapters.commandController.kafka.handlers.PLAYER_CREATED
import com.abaddon83.burraco.game.ports.CommandControllerPort

class KafkaPlayerConsumerVerticle(
    serviceConfig: ServiceConfig,
    private val commandController: CommandControllerPort
) : KafkaConsumerVerticle(serviceConfig.kafkaPlayerConsumer) {

    override fun loadHandlers(): EventRouterHandler = EventRouterHandler()
        .addHandler(PLAYER_CREATED, AddPlayerHandlerKafka(commandController))
}