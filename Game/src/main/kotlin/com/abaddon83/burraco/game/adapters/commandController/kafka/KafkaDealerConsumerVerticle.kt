package com.abaddon83.burraco.game.adapters.commandController.kafka

import com.abaddon83.burraco.common.adapter.kafka.consumer.EventRouterHandler
import com.abaddon83.burraco.common.adapter.kafka.consumer.KafkaConsumerVerticle
import com.abaddon83.burraco.game.ServiceConfig
import com.abaddon83.burraco.game.adapters.commandController.kafka.handlers.AddCardDeckHandlerKafka
import com.abaddon83.burraco.game.adapters.commandController.kafka.handlers.AddCardDiscardDeckHandlerKafka
import com.abaddon83.burraco.game.adapters.commandController.kafka.handlers.AddCardPlayerHandlerKafka
import com.abaddon83.burraco.game.adapters.commandController.kafka.handlers.AddCardPlayerDeck1HandlerKafka
import com.abaddon83.burraco.game.adapters.commandController.kafka.handlers.AddCardPlayerDeck2HandlerKafka
import com.abaddon83.burraco.game.ports.CommandControllerPort


class KafkaDealerConsumerVerticle(
    serviceConfig: ServiceConfig,
    private val commandController: CommandControllerPort
) : KafkaConsumerVerticle(serviceConfig.kafkaDealerConsumer) {

    override fun loadHandlers(): EventRouterHandler = EventRouterHandler()
        .addHandler("CardDealtToDeck", AddCardDeckHandlerKafka(commandController))
        .addHandler("CardDealtToPlayer", AddCardPlayerHandlerKafka(commandController))
        .addHandler("CardDealtToPlayerDeck1", AddCardPlayerDeck1HandlerKafka(commandController))
        .addHandler("CardDealtToPlayerDeck2", AddCardPlayerDeck2HandlerKafka(commandController))
        .addHandler("CardDealtToDiscardDeck", AddCardDiscardDeckHandlerKafka(commandController))
}