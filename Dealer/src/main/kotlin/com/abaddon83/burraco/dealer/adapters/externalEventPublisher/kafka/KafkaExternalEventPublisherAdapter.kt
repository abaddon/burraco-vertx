package com.abaddon83.burraco.dealer.adapters.externalEventPublisher.kafka

import com.abaddon83.burraco.common.VertxCoroutineScope
import com.abaddon83.burraco.common.adapter.kafka.producer.KafkaProducerConfig
import com.abaddon83.burraco.common.adapter.kafka.producer.KafkaProducerVerticle
import com.abaddon83.burraco.common.externalEvents.ExternalEvent
import com.abaddon83.burraco.common.externalEvents.dealer.CardGivenToDeck
import com.abaddon83.burraco.dealer.events.CardDealtToDeck
import com.abaddon83.burraco.dealer.events.DealerEvent
import com.abaddon83.burraco.dealer.models.Dealer
import com.abaddon83.burraco.dealer.ports.ExternalEventPublisherPort
import io.vertx.core.impl.logging.Logger
import io.vertx.core.impl.logging.LoggerFactory
import kotlinx.coroutines.withContext

class KafkaExternalEventPublisherAdapter(
    vertxCoroutineScope: VertxCoroutineScope,
    kafkaConfig: KafkaProducerConfig
) : KafkaProducerVerticle<DealerEvent, Dealer>(vertxCoroutineScope, kafkaConfig), ExternalEventPublisherPort {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun chooseExternalEvent(aggregate: Dealer, domainEvent: DealerEvent): ExternalEvent? {
        return when (domainEvent) {
            is CardDealtToDeck -> CardGivenToDeck(domainEvent.aggregateId, domainEvent.gameId, domainEvent.card.label())
            //is CardDealtToDiscardDeck -> {}
            //is CardDealtToPlayer -> {}
            //is CardDealtToPlayerDeck1 -> {}
            //is CardDealtToPlayerDeck2 -> {}
            else -> null
        }
    }

    override suspend fun publish(
        aggregate: Dealer,
        event: DealerEvent
    ): Result<Unit> = withContext(vertxCoroutineScope.coroutineContext()) {
        publishOnKafka(aggregate, event)
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