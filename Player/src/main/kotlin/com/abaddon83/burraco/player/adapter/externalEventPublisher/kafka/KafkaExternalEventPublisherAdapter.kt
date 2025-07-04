package com.abaddon83.burraco.player.adapter.externalEventPublisher.kafka

import com.abaddon83.burraco.common.adapter.kafka.producer.KafkaProducerConfig
import com.abaddon83.burraco.common.adapter.kafka.producer.KafkaProducerVerticle
import com.abaddon83.burraco.common.externalEvents.ExternalEvent
import com.abaddon83.burraco.common.externalEvents.player.PlayerCreated as PlayerCreatedExternalEvent
import com.abaddon83.burraco.common.models.event.player.PlayerCreated
import com.abaddon83.burraco.common.models.event.player.PlayerEvent
import com.abaddon83.burraco.player.model.player.Player
import com.abaddon83.burraco.player.port.ExternalEventPublisherPort
import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log
import io.vertx.core.Vertx
import io.vertx.kafka.client.producer.KafkaProducer

class KafkaExternalEventPublisherAdapter(
    vertx: Vertx,
    kafkaConfig: KafkaProducerConfig
) : KafkaProducerVerticle<PlayerEvent, Player>(
    KafkaProducer.create(vertx, kafkaConfig.producerConfig()),
    kafkaConfig.topic()
), ExternalEventPublisherPort {

    override fun chooseExternalEvent(aggregate: Player, domainEvent: PlayerEvent): ExternalEvent? {
        return when (domainEvent) {
            is PlayerCreated -> {
                PlayerCreatedExternalEvent(
                    domainEvent.aggregateId,
                    domainEvent.gameIdentity
                )
            }
            else -> null
        }
    }

    override suspend fun publish(aggregate: Player, event: PlayerEvent): Result<Unit> = runCatching {
        publishOnKafka(aggregate, event)
    }

    override fun onFailure(
        throwable: Throwable,
        aggregate: Player,
        domainEvent: PlayerEvent,
        extEvent: ExternalEvent
    ): Result<Unit> {
        log.error("Event ${domainEvent.javaClass.simpleName} not published to Kafka", throwable)
        return Result.failure(Exception(throwable))
    }

    override fun onSuccess(aggregate: Player, domainEvent: PlayerEvent, extEvent: ExternalEvent): Result<Unit> {
        log.info("ExternalEvent ${extEvent.javaClass.simpleName} published")
        return Result.success(Unit)
    }
}