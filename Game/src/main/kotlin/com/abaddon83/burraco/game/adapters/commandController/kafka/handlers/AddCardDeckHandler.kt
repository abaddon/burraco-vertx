package com.abaddon83.burraco.game.adapters.commandController.kafka.handlers

import com.abaddon83.burraco.common.adapter.kafka.KafkaEvent
import com.abaddon83.burraco.common.adapter.kafka.consumer.EventHandler
import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.game.ports.CommandControllerPort
import io.vertx.core.Vertx

class AddCardDeckHandler(private val controllerAdapter: CommandControllerPort, vertx: Vertx): EventHandler(vertx) {

    override suspend fun asyncHandle(event: KafkaEvent?) {
        val gameIdentity= GameIdentity.empty();
        val card= Card.fromLabel("")
        controllerAdapter.addCardDeck(gameIdentity,card)
    }
}