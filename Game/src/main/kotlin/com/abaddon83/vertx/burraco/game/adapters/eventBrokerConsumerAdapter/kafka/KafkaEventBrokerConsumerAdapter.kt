package com.abaddon83.vertx.burraco.game.adapters.eventBrokerConsumerAdapter.kafka

import com.abaddon83.vertx.burraco.game.adapters.eventBrokerConsumerAdapter.kafka.config.KafkaConsumerConfig
import com.abaddon83.vertx.burraco.game.ports.EventBrokerConsumerPort
import io.vertx.core.Vertx
import io.vertx.kafka.client.consumer.KafkaConsumer


class KafkaEventBrokerConsumerAdapter<K,V>(val vertx: Vertx, private val kafkaCondig: KafkaConsumerConfig): EventBrokerConsumerPort {

    val consumer: KafkaConsumer<K,V> =  KafkaConsumer.create(vertx, kafkaCondig.consumerConfig());
}