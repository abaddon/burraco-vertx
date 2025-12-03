package com.abaddon83.burraco.common.adapter.kafka.producer

import com.abaddon83.burraco.common.externalEvents.ExternalEvent
import com.abaddon83.burraco.common.externalEvents.KafkaEvent
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
        val externalEvent = chooseExternalEvent(aggregate, domainEvent) ?: return Result.success(Unit)

        val kafkaEvent = KafkaEvent.from(externalEvent)
        // Phase 3 Optimization: Use gameId as partition key for horizontal scaling
        val partitionKey = externalEvent.extractEventKey()
        val record = KafkaProducerRecord.create(
            topic,
            partitionKey,
            kafkaEvent.toJson()
        )
        producer.write(record)
            .onSuccess {
                onSuccess(aggregate, domainEvent, externalEvent)
            }.coAwait()
    }

    /**
     * Phase 2 Optimization: Batch publish multiple events at once.
     * Phase 3 Enhancement: Uses gameId as partition key for horizontal scaling.
     * With Phase 1 Kafka config (linger.ms=10, batch.size=32KB), messages will be
     * batched by Kafka producer automatically, reducing network calls.
     * All events for the same game go to the same partition, maintaining ordering.
     */
    suspend fun publishBatchOnKafka(aggregate: AR, domainEvents: List<DE>): Result<Unit> = runCatching {
        // Convert domain events to Kafka records
        val records = domainEvents.mapNotNull { domainEvent ->
            val externalEvent = chooseExternalEvent(aggregate, domainEvent) ?: return@mapNotNull null
            val kafkaEvent = KafkaEvent.from(externalEvent)
            // Phase 3 Optimization: Use gameId as partition key for horizontal scaling
            val partitionKey = externalEvent.extractEventKey()
            KafkaProducerRecord.create(
                topic,
                partitionKey,
                kafkaEvent.toJson()
            ) to Pair(domainEvent, externalEvent)
        }

        // Publish all records (Kafka will batch them based on Phase 1 config)
        records.forEach { (record, eventPair) ->
            val (domainEvent, externalEvent) = eventPair
            producer.write(record)
                .onSuccess {
                    onSuccess(aggregate, domainEvent, externalEvent)
                }.coAwait()
        }
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