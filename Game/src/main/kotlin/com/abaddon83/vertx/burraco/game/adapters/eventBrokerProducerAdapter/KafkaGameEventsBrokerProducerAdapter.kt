package com.abaddon83.vertx.burraco.game.adapters.eventBrokerProducerAdapter

import com.abaddon83.burraco.common.events.ExtendEvent
import com.abaddon83.utils.ddd.Event
import com.abaddon83.vertx.burraco.game.adapters.eventBrokerProducerAdapter.config.KafkaProducerConfig
import com.abaddon83.vertx.burraco.game.ports.GameEventsBrokerProducerPort
import io.vertx.core.Vertx
import io.vertx.kafka.client.producer.KafkaProducer
import io.vertx.kafka.client.producer.KafkaProducerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class KafkaGameEventsBrokerProducerAdapter(val vertx: Vertx, private val kafkaCondig: KafkaProducerConfig): GameEventsBrokerProducerPort<String,String> {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)
    private val producer: KafkaProducer<String,String> =  KafkaProducer.create(vertx, kafkaCondig.producerConfig());

    override fun publish(topic: String, event: Event){
        val extendEvent = ExtendEvent(event)
        val record = KafkaProducerRecord.create(topic, extendEvent.entityKey.toString(), extendEvent.toJsonString())
        producer.write(record)
            .onFailure{
                log.error("message not published to Kafka",it)
            }
    }
}