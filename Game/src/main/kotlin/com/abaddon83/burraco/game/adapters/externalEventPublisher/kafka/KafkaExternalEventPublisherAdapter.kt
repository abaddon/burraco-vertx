package com.abaddon83.burraco.game.adapters.externalEventPublisher.kafka

import com.abaddon83.burraco.common.adapter.kafka.producer.KafkaProducerConfig
import com.abaddon83.burraco.common.adapter.kafka.producer.KafkaProducerVerticle
import com.abaddon83.burraco.common.externalEvents.ExternalEvent
import com.abaddon83.burraco.common.externalEvents.game.CardsRequestedToDealerExternalEvent
import com.abaddon83.burraco.common.externalEvents.game.GameCreatedExternalEvent
import com.abaddon83.burraco.common.models.event.game.CardDealingRequested
import com.abaddon83.burraco.common.models.event.game.GameCreated
import com.abaddon83.burraco.common.models.event.game.GameEvent
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameWaitingDealer
import com.abaddon83.burraco.game.ports.ExternalEventPublisherPort
import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log
import io.vertx.core.Vertx
import io.vertx.kafka.client.producer.KafkaProducer

class KafkaExternalEventPublisherAdapter(
    vertx: Vertx,
    kafkaConfig: KafkaProducerConfig
) : KafkaProducerVerticle<GameEvent, Game>(
    KafkaProducer.create(vertx, kafkaConfig.producerConfig()),
    kafkaConfig.topic()
), ExternalEventPublisherPort {

    override fun chooseExternalEvent(aggregate: Game, domainEvent: GameEvent): ExternalEvent? {
        return when (domainEvent) {
            is CardDealingRequested -> {
                val listPlayerIdentities = (aggregate as GameWaitingDealer).players.map { it.id }
                CardsRequestedToDealerExternalEvent(domainEvent.aggregateId, listPlayerIdentities)
            }

            is GameCreated -> {
                // GameCreated event does not have an associated ExternalEvent
                GameCreatedExternalEvent(domainEvent.aggregateId)
            }

            else -> null
        }
    }

    override suspend fun publish(
        aggregate: Game,
        event: GameEvent
    ): Result<Unit> = runCatching {
        publishOnKafka(aggregate, event)
    }


    override fun onFailure(
        throwable: Throwable,
        aggregate: Game,
        domainEvent: GameEvent,
        extEvent: ExternalEvent
    ): Result<Unit> {
        log.error("Event ${domainEvent.javaClass.simpleName} not published to Kafka", throwable)
        return Result.failure(Exception(throwable))
    }

    override fun onSuccess(aggregate: Game, domainEvent: GameEvent, extEvent: ExternalEvent): Result<Unit> {
        log.info("ExternalEvent  ${extEvent.javaClass.simpleName} published")
        return Result.success(Unit)
    }


}