package com.abaddon83.burraco.game.adapters.gameEventPublisher.kafka

import com.abaddon83.burraco.common.adapter.kafka.producer.KafkaProducerConfig
import com.abaddon83.burraco.common.adapter.kafka.producer.KafkaProducerVerticle
import com.abaddon83.burraco.common.externalEvents.ExternalEvent
import com.abaddon83.burraco.common.externalEvents.game.CardsRequestedToDealer
import com.abaddon83.burraco.game.events.game.CardDealingRequested
import com.abaddon83.burraco.game.events.game.GameEvent
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameWaitingDealer
import com.abaddon83.burraco.game.ports.GameEventPublisherPort

import io.vertx.core.Vertx
import io.vertx.core.impl.logging.Logger
import io.vertx.core.impl.logging.LoggerFactory

class KafkaGameEventPublisherAdapter(
    vertx: Vertx,
    kafkaConfig: KafkaProducerConfig
): KafkaProducerVerticle<GameEvent,Game>(vertx, kafkaConfig), GameEventPublisherPort {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)
    override fun chooseExternalEvent(aggregate: Game, domainEvent: GameEvent): ExternalEvent? {
        return when(domainEvent){
            is CardDealingRequested -> {
                val listPlayerIdentities=(aggregate as GameWaitingDealer).players.map { it.id }
                CardsRequestedToDealer(domainEvent.aggregateId,listPlayerIdentities)
            }
            else -> null
        }
    }

    override fun onFailure(throwable: Throwable, aggregate: Game, domainEvent: GameEvent, extEvent: ExternalEvent) {
        log.error("Event ${domainEvent.javaClass.simpleName} not published to Kafka", throwable)
    }

    override fun onSuccess(aggregate: Game, domainEvent: GameEvent, extEvent: ExternalEvent) {
        log.info("ExternalEvent  ${extEvent.javaClass.simpleName} published")
    }

}