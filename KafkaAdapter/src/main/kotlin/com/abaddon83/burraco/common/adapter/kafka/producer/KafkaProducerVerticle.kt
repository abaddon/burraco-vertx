package com.abaddon83.burraco.common.adapter.kafka.producer

import com.abaddon83.burraco.common.adapter.kafka.KafkaEvent
import com.abaddon83.burraco.common.externalEvents.ExternalEvent
import io.github.abaddon.kcqrs.core.domain.AggregateRoot
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.vertx.kafka.client.producer.KafkaProducer
import io.vertx.kafka.client.producer.KafkaProducerRecord
import io.vertx.kotlin.coroutines.coAwait


abstract class KafkaProducerVerticle<DE : IDomainEvent, AR : AggregateRoot>(
    private val producer: KafkaProducer<String, String>,
    private val topic: String
) {

    abstract fun chooseExternalEvent(aggregate: AR, domainEvent: DE): ExternalEvent?

    suspend fun publishOnKafka(aggregate: AR, domainEvent: DE): Result<Unit> = runCatching {
        val externalEvent = chooseExternalEvent(aggregate, domainEvent)

        if (externalEvent == null) {
            return Result.success(Unit)
        }

        val kafkaEvent = KafkaEvent.from(externalEvent)
        val record = KafkaProducerRecord.create(
            topic,
            externalEvent.aggregateIdentity.valueAsString(),
            kafkaEvent.toJson()
        )
        producer.write(record)
            .onSuccess {
                onSuccess(aggregate, domainEvent, externalEvent)
            }.coAwait()
    }

    /**
     * Graceful shutdown of the producer
     */
    fun close(): Result<Unit> = runCatching {
        producer.close()
    }

    abstract fun onFailure(throwable: Throwable, aggregate: AR, domainEvent: DE, extEvent: ExternalEvent): Result<Unit>

    abstract fun onSuccess(aggregate: AR, domainEvent: DE, extEvent: ExternalEvent): Result<Unit>

}