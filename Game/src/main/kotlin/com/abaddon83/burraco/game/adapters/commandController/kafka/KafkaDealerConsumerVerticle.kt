package com.abaddon83.burraco.game.adapters.commandController.kafka

import com.abaddon83.burraco.common.adapter.kafka.consumer.EventRouterHandler
import com.abaddon83.burraco.common.adapter.kafka.consumer.KafkaConsumerConfig
import com.abaddon83.burraco.common.adapter.kafka.consumer.KafkaConsumerVerticle
import com.abaddon83.burraco.game.adapters.commandController.kafka.handlers.AddCardDeckHandler
import com.abaddon83.burraco.game.ports.CommandControllerPort


class KafkaDealerConsumerVerticle(
    kafkaConfig: KafkaConsumerConfig,
    private val controllerAdapter: CommandControllerPort
) : KafkaConsumerVerticle(kafkaConfig) {

    override fun loadHandlers(): EventRouterHandler = EventRouterHandler()
        .addHandler("DeckCardDealt", AddCardDeckHandler(controllerAdapter))

}