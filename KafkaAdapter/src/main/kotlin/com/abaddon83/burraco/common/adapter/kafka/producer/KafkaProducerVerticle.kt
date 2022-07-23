package com.abaddon83.burraco.common.adapter.kafka.producer

import com.abaddon83.burraco.common.adapter.kafka.KafkaEvent
import com.abaddon83.burraco.common.externalEvents.ExternalEvent
import io.github.abaddon.kcqrs.core.domain.AggregateRoot
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.vertx.core.Vertx
import io.vertx.kafka.client.producer.KafkaProducer
import io.vertx.kafka.client.producer.KafkaProducerRecord


abstract class KafkaProducerVerticle<DE: IDomainEvent, AR: AggregateRoot>(vertx: Vertx, kafkaConfig: KafkaProducerConfig) {
    private val producer: KafkaProducer<String, String> = KafkaProducer.create(vertx, kafkaConfig.producerConfig());
    private val topic: String = kafkaConfig.topic();

    abstract fun chooseExternalEvent(aggregate: AR, domainEvent: DE): ExternalEvent?

    suspend fun publish(aggregate: AR, domainEvent: DE) {

        val externalEvent = chooseExternalEvent(aggregate,domainEvent)

        externalEvent?.let { extEvent: ExternalEvent ->
            val kafkaEvent = KafkaEvent.from(extEvent)
            val record = KafkaProducerRecord.create(topic, extEvent.aggregateIdentity.valueAsString(), kafkaEvent.toJson())
            producer .write(record)
                .onFailure {
                    onFailure(it, aggregate,domainEvent,extEvent)
                }.onSuccess {
                    onSuccess(aggregate,domainEvent,extEvent)
                }
        }
    }

    abstract fun onFailure(throwable: Throwable, aggregate: AR, domainEvent: DE, extEvent: ExternalEvent)

    abstract fun onSuccess(aggregate: AR, domainEvent: DE, extEvent: ExternalEvent)

}