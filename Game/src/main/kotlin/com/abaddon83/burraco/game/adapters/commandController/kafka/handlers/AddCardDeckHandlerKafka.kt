package com.abaddon83.burraco.game.adapters.commandController.kafka.handlers

import com.abaddon83.burraco.common.adapter.kafka.KafkaEvent
import com.abaddon83.burraco.common.adapter.kafka.consumer.KafkaEventHandler
import com.abaddon83.burraco.common.externalEvents.dealer.CardGivenToDeck
import com.abaddon83.burraco.common.helpers.Validated
import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.ports.CommandControllerPort
import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log
import io.vertx.core.json.Json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class AddCardDeckHandlerKafka(private val controllerAdapter: CommandControllerPort) : KafkaEventHandler() {


    override fun getCoroutineIOScope(): CoroutineScope =
        CoroutineScope(Dispatchers.IO + SupervisorJob())

    override suspend fun handleKafkaEventRequest(event: KafkaEvent?): Validated<*, *> {
        checkNotNull(event)
        check(event.eventName == CardGivenToDeck::class.java.simpleName)
        log.info("Event ${event.eventName} received")
        val cardGivenToDeckEvent = Json.decodeValue(event.eventPayload, CardGivenToDeck::class.java)
        val card = Card.fromLabel(cardGivenToDeckEvent.cardLabel)
        val gameIdentity = cardGivenToDeckEvent.gameIdentity

        return controllerAdapter.addCardDeck(gameIdentity, card)
    }


}