package com.abaddon83.burraco.game.adapters.gameEventPublisher.kafka

import com.abaddon83.burraco.game.events.game.GameEvent
import com.abaddon83.burraco.game.ports.GameEventPublisherPort
import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import io.vertx.kafka.client.producer.KafkaProducer
import io.vertx.kafka.client.producer.KafkaProducerRecord
import io.vertx.kotlin.coroutines.CoroutineVerticle
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class KafkaGameEventPublisherAdapter(vertx: Vertx, kafkaConfig: KafkaProducerConfig) : GameEventPublisherPort {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)
    private val producer: KafkaProducer<String, String> = KafkaProducer.create(vertx, kafkaConfig.producerConfig());
    private val topic: String = kafkaConfig.topic();

    override suspend fun publish(event: GameEvent) {
        val kafkaEvent = KafkaEvent.from(event)
        kafkaEvent?.let { kevent ->
            val record =
                KafkaProducerRecord.create(topic, event.aggregateId.valueAsString(), kevent.toJson())
            producer.write(record)
                .onFailure {
                    onFailure(it, event)
                }.onSuccess {
                    onSuccess(event)
                }
        }
    }

    override fun onFailure(throwable: Throwable, event: GameEvent) {
        log.error("Event ${event.javaClass.simpleName} not published to Kafka", throwable)
    }

    override fun onSuccess(event: GameEvent) {
        log.info("Event  ${event.javaClass.simpleName} published")
    }

}