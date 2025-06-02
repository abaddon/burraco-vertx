package com.abaddon83.burraco.game.adapters.commandController.kafka

import com.abaddon83.burraco.common.adapter.kafka.consumer.EventRouterHandler
import com.abaddon83.burraco.common.adapter.kafka.consumer.KafkaConsumerVerticle
import com.abaddon83.burraco.game.ServiceConfig
import com.abaddon83.burraco.game.adapters.commandController.kafka.handlers.AddCardDeckHandler
import com.abaddon83.burraco.game.ports.CommandControllerPort
import kotlinx.coroutines.CoroutineScope


class KafkaDealerConsumerVerticle(
    serviceConfig: ServiceConfig,
    private val controllerAdapter: CommandControllerPort
) : KafkaConsumerVerticle(serviceConfig.kafkaDealerConsumer) {

    override fun loadHandlers(): EventRouterHandler = EventRouterHandler()
        .addHandler("CardGivenToDeck", AddCardDeckHandler(controllerAdapter, vertx))

}