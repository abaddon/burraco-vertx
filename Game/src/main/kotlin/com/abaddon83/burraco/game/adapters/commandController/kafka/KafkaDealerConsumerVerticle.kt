package com.abaddon83.burraco.game.adapters.commandController.kafka

import com.abaddon83.burraco.common.adapter.kafka.consumer.EventRouterHandler
import com.abaddon83.burraco.common.adapter.kafka.consumer.KafkaConsumerVerticle
import com.abaddon83.burraco.game.ServiceConfig
import com.abaddon83.burraco.game.adapters.commandController.kafka.handlers.*
import com.abaddon83.burraco.game.ports.CommandControllerPort


class KafkaDealerConsumerVerticle(
    serviceConfig: ServiceConfig,
    private val commandController: CommandControllerPort
) : KafkaConsumerVerticle(serviceConfig.kafkaDealerConsumer) {

    override fun loadHandlers(): EventRouterHandler = EventRouterHandler()
        .addHandler(AddCardDeckHandlerKafka(commandController))
        .addHandler(AddCardPlayerHandlerKafka(commandController))
        .addHandler(AddCardPlayerDeck1HandlerKafka(commandController))
        .addHandler(AddCardPlayerDeck2HandlerKafka(commandController))
        .addHandler(AddCardDiscardDeckHandlerKafka(commandController))
        .addHandler(StartGameHandlerKafka(commandController))
}