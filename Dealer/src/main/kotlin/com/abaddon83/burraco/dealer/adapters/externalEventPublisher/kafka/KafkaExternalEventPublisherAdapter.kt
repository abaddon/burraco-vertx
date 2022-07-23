package com.abaddon83.burraco.dealer.adapters.externalEventPublisher.kafka

import com.abaddon83.burraco.common.adapter.kafka.producer.KafkaProducerConfig
import com.abaddon83.burraco.common.adapter.kafka.producer.KafkaProducerVerticle
import com.abaddon83.burraco.common.externalEvents.ExternalEvent
import com.abaddon83.burraco.common.externalEvents.dealer.CardGivenToDeck
import com.abaddon83.burraco.dealer.events.*
import com.abaddon83.burraco.dealer.models.Dealer
import com.abaddon83.burraco.dealer.ports.ExternalEventPublisherPort
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.Logger
import io.vertx.core.impl.logging.LoggerFactory

class KafkaExternalEventPublisherAdapter(
    vertx: Vertx,
    kafkaConfig: KafkaProducerConfig
): KafkaProducerVerticle<DealerEvent, Dealer>(vertx, kafkaConfig), ExternalEventPublisherPort {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun chooseExternalEvent(aggregate: Dealer, domainEvent: DealerEvent): ExternalEvent? {
        return when(domainEvent){
           is CardDealtToDeck -> CardGivenToDeck(domainEvent.aggregateId,domainEvent.gameId,domainEvent.card.label())
            //is CardDealtToDiscardDeck -> {}
            //is CardDealtToPlayer -> {}
            //is CardDealtToPlayerDeck1 -> {}
            //is CardDealtToPlayerDeck2 -> {}
            else -> null
        }
    }

    override fun onFailure(throwable: Throwable, aggregate: Dealer, domainEvent: DealerEvent, extEvent: ExternalEvent) {
        log.error("Event ${domainEvent.javaClass.simpleName} not published to Kafka", throwable)
    }

    override fun onSuccess(aggregate: Dealer, domainEvent: DealerEvent, extEvent: ExternalEvent) {
        log.info("ExternalEvent  ${extEvent.javaClass.simpleName} published")
    }

}