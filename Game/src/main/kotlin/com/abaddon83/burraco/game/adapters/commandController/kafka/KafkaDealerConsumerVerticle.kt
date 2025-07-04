package com.abaddon83.burraco.game.adapters.commandController.kafka

import com.abaddon83.burraco.common.adapter.kafka.consumer.EventRouterHandler
import com.abaddon83.burraco.common.adapter.kafka.consumer.KafkaConsumerVerticle
import com.abaddon83.burraco.game.ServiceConfig
import com.abaddon83.burraco.game.adapters.commandController.kafka.handlers.AddCardDeckHandlerKafka
import com.abaddon83.burraco.game.adapters.commandController.kafka.handlers.AddCardDiscardDeckHandlerKafka
import com.abaddon83.burraco.game.adapters.commandController.kafka.handlers.AddCardPlayerDeck1HandlerKafka
import com.abaddon83.burraco.game.adapters.commandController.kafka.handlers.AddCardPlayerDeck2HandlerKafka
import com.abaddon83.burraco.game.adapters.commandController.kafka.handlers.AddCardPlayerHandlerKafka
import com.abaddon83.burraco.game.adapters.commandController.kafka.handlers.CARD_DEALT_TO_DECK
import com.abaddon83.burraco.game.adapters.commandController.kafka.handlers.CARD_DEALT_TO_DISCARD_DECK
import com.abaddon83.burraco.game.adapters.commandController.kafka.handlers.CARD_DEALT_TO_PLAYER
import com.abaddon83.burraco.game.adapters.commandController.kafka.handlers.CARD_DEALT_TO_PLAYER_DECK1
import com.abaddon83.burraco.game.adapters.commandController.kafka.handlers.CARD_DEALT_TO_PLAYER_DECK2
import com.abaddon83.burraco.game.ports.CommandControllerPort


class KafkaDealerConsumerVerticle(
    serviceConfig: ServiceConfig,
    private val commandController: CommandControllerPort
) : KafkaConsumerVerticle(serviceConfig.kafkaDealerConsumer) {

    override fun loadHandlers(): EventRouterHandler = EventRouterHandler()
        .addHandler(CARD_DEALT_TO_DECK, AddCardDeckHandlerKafka(commandController))
        .addHandler(CARD_DEALT_TO_PLAYER, AddCardPlayerHandlerKafka(commandController))
        .addHandler(CARD_DEALT_TO_PLAYER_DECK1, AddCardPlayerDeck1HandlerKafka(commandController))
        .addHandler(CARD_DEALT_TO_PLAYER_DECK2, AddCardPlayerDeck2HandlerKafka(commandController))
        .addHandler(CARD_DEALT_TO_DISCARD_DECK, AddCardDiscardDeckHandlerKafka(commandController))
}