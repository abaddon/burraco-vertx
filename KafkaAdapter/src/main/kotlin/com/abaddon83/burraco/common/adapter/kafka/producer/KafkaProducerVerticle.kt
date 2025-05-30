package com.abaddon83.burraco.common.adapter.kafka.producer

import com.abaddon83.burraco.common.VertxCoroutineScope
import com.abaddon83.burraco.common.adapter.kafka.KafkaEvent
import com.abaddon83.burraco.common.externalEvents.ExternalEvent
import io.github.abaddon.kcqrs.core.domain.AggregateRoot
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.vertx.kafka.client.producer.KafkaProducer
import io.vertx.kafka.client.producer.KafkaProducerRecord
import kotlinx.coroutines.withContext


abstract class KafkaProducerVerticle<DE : IDomainEvent, AR : AggregateRoot>(
    protected val vertxCoroutineScope: VertxCoroutineScope,
    kafkaConfig: KafkaProducerConfig
) {
    private val producer: KafkaProducer<String, String> =
        KafkaProducer.create(vertxCoroutineScope.vertx, kafkaConfig.producerConfig());
    private val topic: String = kafkaConfig.topic();

    abstract fun chooseExternalEvent(aggregate: AR, domainEvent: DE): ExternalEvent?

    suspend fun publishOnKafka(aggregate: AR, domainEvent: DE): Result<Unit> = runCatching {
        withContext(vertxCoroutineScope.coroutineContext()) {
            val externalEvent = chooseExternalEvent(aggregate, domainEvent)

            if (externalEvent == null) {
                return@withContext Result.success(Unit)
            }


            val kafkaEvent = KafkaEvent.from(externalEvent)
            val record = KafkaProducerRecord.create(
                topic,
                externalEvent.aggregateIdentity.valueAsString(),
                kafkaEvent.toJson()
            )

            // Convert Vert.x Future to suspending call
            val writeResult = producer.write(record)
            onSuccess(aggregate, domainEvent, externalEvent)
            Result.success(Unit)
        }
    }.getOrElse { exception ->
        // Handle any outer exceptions (context switching, etc.)
        Result.failure(exception)
    }

//    suspend fun publish(aggregate: AR, domainEvent: DE): Result<Unit> =
//        withContext(vertxCoroutineScope.coroutineContext()) {
//
//            val externalEvent = chooseExternalEvent(aggregate, domainEvent)
//
//            externalEvent?.let { extEvent: ExternalEvent ->
//                val kafkaEvent = KafkaEvent.from(extEvent)
//                val record =
//                    KafkaProducerRecord.create(topic, extEvent.aggregateIdentity.valueAsString(), kafkaEvent.toJson())
//                producer.write(record)
//                    .onFailure {
//                        return@onFailure onFailure(it, aggregate, domainEvent, extEvent)
//                    }.onSuccess {
//                        return@onSuccess onSuccess(aggregate, domainEvent, extEvent)
//                    }
//            }
//        }

    /**
     * Graceful shutdown of the producer
     */
    suspend fun close(): Result<Unit> = runCatching {
        withContext(vertxCoroutineScope.coroutineContext()) {
            producer.close()
        }
    }

    abstract fun onFailure(throwable: Throwable, aggregate: AR, domainEvent: DE, extEvent: ExternalEvent): Result<Unit>

    abstract fun onSuccess(aggregate: AR, domainEvent: DE, extEvent: ExternalEvent): Result<Unit>

}