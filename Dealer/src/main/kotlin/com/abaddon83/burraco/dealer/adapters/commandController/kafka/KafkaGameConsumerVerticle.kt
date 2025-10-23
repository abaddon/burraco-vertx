package com.abaddon83.burraco.dealer.adapters.commandController.kafka

import com.abaddon83.burraco.common.adapter.kafka.consumer.EventRouterHandler
import com.abaddon83.burraco.common.adapter.kafka.consumer.KafkaConsumerVerticle
import com.abaddon83.burraco.dealer.ServiceConfig
import com.abaddon83.burraco.dealer.adapters.commandController.kafka.handlers.CardsRequestedToDealerHandlerKafka
import com.abaddon83.burraco.dealer.ports.CommandControllerPort

class KafkaGameConsumerVerticle(
    serviceConfig: ServiceConfig,
    private val commandController: CommandControllerPort
) : KafkaConsumerVerticle(serviceConfig.gameEventConsumer) {

    override fun loadHandlers(): EventRouterHandler = EventRouterHandler()
        .addHandler(CardsRequestedToDealerHandlerKafka(commandController))

}