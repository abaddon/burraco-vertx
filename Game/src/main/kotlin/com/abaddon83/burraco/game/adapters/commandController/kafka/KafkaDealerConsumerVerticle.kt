package com.abaddon83.burraco.game.adapters.commandController.kafka

import com.abaddon83.burraco.common.adapter.kafka.consumer.EventRouterHandler
import com.abaddon83.burraco.common.adapter.kafka.consumer.KafkaConsumerVerticle
import com.abaddon83.burraco.game.ServiceConfig
import com.abaddon83.burraco.game.adapters.commandController.kafka.handlers.AddCardDeckHandlerKafka
import com.abaddon83.burraco.game.adapters.commandController.kafka.handlers.AddCardPlayerHandlerKafka
import com.abaddon83.burraco.game.ports.CommandControllerPort


class KafkaDealerConsumerVerticle(
    serviceConfig: ServiceConfig,
    private val controllerAdapter: CommandControllerPort
) : KafkaConsumerVerticle(serviceConfig.kafkaDealerConsumer) {

    override fun loadHandlers(): EventRouterHandler = EventRouterHandler()
        .addHandler("CardDealtToDeck", AddCardDeckHandlerKafka(controllerAdapter))
        .addHandler("CardDealtToPlayer", AddCardPlayerHandlerKafka(controllerAdapter))

}