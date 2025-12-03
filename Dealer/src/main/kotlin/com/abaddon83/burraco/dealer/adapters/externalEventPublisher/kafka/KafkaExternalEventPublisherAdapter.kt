package com.abaddon83.burraco.dealer.adapters.externalEventPublisher.kafka

import com.abaddon83.burraco.common.adapter.kafka.producer.KafkaProducerConfig
import com.abaddon83.burraco.common.adapter.kafka.producer.KafkaProducerVerticle
import com.abaddon83.burraco.common.externalEvents.ExternalEvent
import com.abaddon83.burraco.common.externalEvents.dealer.CardDealtToDeckExternalEvent
import com.abaddon83.burraco.common.externalEvents.dealer.CardDealtToDiscardDeckExternalEvent
import com.abaddon83.burraco.common.externalEvents.dealer.CardDealtToPlayerDeck1ExternalEvent
import com.abaddon83.burraco.common.externalEvents.dealer.CardDealtToPlayerDeck2ExternalEvent
import com.abaddon83.burraco.common.externalEvents.dealer.CardDealtToPlayerExternalEvent
import com.abaddon83.burraco.common.models.event.dealer.CardDealtToDeck
import com.abaddon83.burraco.common.models.event.dealer.CardDealtToDiscardDeck
import com.abaddon83.burraco.common.models.event.dealer.CardDealtToPlayer
import com.abaddon83.burraco.common.models.event.dealer.CardDealtToPlayerDeck1
import com.abaddon83.burraco.common.models.event.dealer.CardDealtToPlayerDeck2
import com.abaddon83.burraco.common.models.event.dealer.DealerEvent
import com.abaddon83.burraco.dealer.models.Dealer
import com.abaddon83.burraco.dealer.ports.ExternalEventPublisherPort
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.Logger
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.kafka.client.producer.KafkaProducer

class KafkaExternalEventPublisherAdapter(
    vertx: Vertx,
    kafkaConfig: KafkaProducerConfig
) : KafkaProducerVerticle<DealerEvent, Dealer>(
    KafkaProducer.create(vertx, kafkaConfig.producerConfig()),
    kafkaConfig.topic()
), ExternalEventPublisherPort {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun chooseExternalEvent(aggregate: Dealer, domainEvent: DealerEvent): ExternalEvent? {
        return when (domainEvent) {
            is CardDealtToDeck -> CardDealtToDeckExternalEvent(
                domainEvent.aggregateId,
                domainEvent.gameId,
                domainEvent.card.label
            )

            is CardDealtToPlayer -> CardDealtToPlayerExternalEvent(
                domainEvent.aggregateId,
                domainEvent.gameId,
                domainEvent.playerId,
                domainEvent.card.label
            )

            is CardDealtToDiscardDeck -> CardDealtToDiscardDeckExternalEvent(
                domainEvent.aggregateId,
                domainEvent.gameId,
                domainEvent.card.label
            )

            is CardDealtToPlayerDeck1 -> CardDealtToPlayerDeck1ExternalEvent(
                domainEvent.aggregateId,
                domainEvent.gameId,
                domainEvent.card.label
            )

            is CardDealtToPlayerDeck2 -> CardDealtToPlayerDeck2ExternalEvent(
                domainEvent.aggregateId,
                domainEvent.gameId,
                domainEvent.card.label
            )

            else -> null
        }
    }

    override suspend fun publish(
        aggregate: Dealer,
        event: DealerEvent
    ): Result<Unit> = runCatching {
        publishOnKafka(aggregate, event)
    }

    /**
     * Phase 2 Optimization: Batch publish multiple events at once.
     * This reduces Kafka round trips from 86 to 1 for card dealing operations.
     */
    override suspend fun publishBatch(
        aggregate: Dealer,
        events: List<DealerEvent>
    ): Result<Unit> = runCatching {
        log.info("Publishing batch of ${events.size} dealer events")
        publishBatchOnKafka(aggregate, events)
        log.info("Successfully published batch of ${events.size} dealer events")
    }

    override fun onFailure(
        throwable: Throwable,
        aggregate: Dealer,
        domainEvent: DealerEvent,
        extEvent: ExternalEvent
    ): Result<Unit> {
        log.error("Event ${domainEvent.javaClass.simpleName} not published to Kafka", throwable)
        return Result.failure(Exception(throwable))
    }

    override fun onSuccess(aggregate: Dealer, domainEvent: DealerEvent, extEvent: ExternalEvent): Result<Unit> {
        log.info("ExternalEvent  ${extEvent.javaClass.simpleName} published")
        return Result.success(Unit)
    }

}