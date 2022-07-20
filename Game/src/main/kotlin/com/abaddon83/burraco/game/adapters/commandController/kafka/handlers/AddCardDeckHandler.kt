package com.abaddon83.burraco.game.adapters.commandController.kafka.handlers

import com.abaddon83.burraco.game.adapters.commandController.kafka.KafkaEvent
import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.game.GameIdentity
import com.abaddon83.burraco.game.ports.CommandControllerPort
import io.vertx.core.Handler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddCardDeckHandler(private val controllerAdapter: CommandControllerPort): Handler<KafkaEvent> {

    override fun handle(event: KafkaEvent?) {
        GlobalScope.launch() {
            val gameIdentity= GameIdentity.create("");
            val card= Card.fromLabel("")
            controllerAdapter.addCardDeck(gameIdentity,card)
        }

    }
}