package com.abaddon83.burraco.player.adapter.commandController.kafka

import com.abaddon83.burraco.common.adapter.kafka.consumer.EventRouterHandler
import com.abaddon83.burraco.common.adapter.kafka.consumer.KafkaConsumerVerticle
import com.abaddon83.burraco.player.ServiceConfig
import com.abaddon83.burraco.player.adapter.commandController.kafka.handlers.AddCardToPlayerHandlerKafka
import com.abaddon83.burraco.player.adapter.commandController.kafka.handlers.CARD_DEALT_TO_PLAYER
import com.abaddon83.burraco.player.port.CommandControllerPort

class KafkaDealerConsumerVerticle(
    serviceConfig: ServiceConfig,
    private val commandController: CommandControllerPort
) : KafkaConsumerVerticle(serviceConfig.kafkaDealerConsumer) {

    override fun loadHandlers(): EventRouterHandler = EventRouterHandler()
        .addHandler(CARD_DEALT_TO_PLAYER, AddCardToPlayerHandlerKafka(commandController))
}
