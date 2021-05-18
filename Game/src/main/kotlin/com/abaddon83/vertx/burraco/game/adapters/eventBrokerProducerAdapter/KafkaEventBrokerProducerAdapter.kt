package com.abaddon83.vertx.burraco.game.adapters.eventBrokerProducerAdapter

import com.abaddon83.vertx.burraco.game.adapters.eventBrokerConsumerAdapter.kafka.config.KafkaProducerConfig
import com.abaddon83.vertx.burraco.game.ports.EventBrokerProducerPort
import io.vertx.core.Vertx
import io.vertx.kafka.client.producer.KafkaProducer
import io.vertx.kafka.client.producer.KafkaProducerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class KafkaEventBrokerProducerAdapter<K,V>(val vertx: Vertx, private val kafkaCondig: KafkaProducerConfig): EventBrokerProducerPort<K,V> {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)
    private val producer: KafkaProducer<K,V> =  KafkaProducer.create(vertx, kafkaCondig.producerConfig());

    override fun publish(topic: String, key: K, value: V){
        val record = KafkaProducerRecord.create<K, V>(topic, key, value)
        producer.write(record)
            .onFailure{
                log.error("message not published to Kafka",it)
            }
    }

}