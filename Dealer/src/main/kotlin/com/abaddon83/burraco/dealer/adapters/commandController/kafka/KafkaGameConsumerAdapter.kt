package com.abaddon83.burraco.dealer.adapters.commandController.kafka

import com.abaddon83.burraco.common.adapter.kafka.consumer.EventRouterHandler
import com.abaddon83.burraco.common.adapter.kafka.consumer.KafkaConsumerVerticle
import com.abaddon83.burraco.dealer.ServiceConfig
import com.abaddon83.burraco.dealer.adapters.commandController.kafka.handlers.CardsRequestedToDealerHandler
import com.abaddon83.burraco.dealer.ports.CommandControllerPort
import io.vertx.kotlin.coroutines.dispatcher

class KafkaGameConsumerAdapter(
    serviceConfig: ServiceConfig,
    private val commandController: CommandControllerPort
) : KafkaConsumerVerticle(serviceConfig.gameEventConsumer) {

    override fun loadHandlers(): EventRouterHandler = EventRouterHandler()
        .addHandler("CardsRequestedToDealer", CardsRequestedToDealerHandler(commandController))

}