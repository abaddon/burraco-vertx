package com.abaddon83.burraco.common.adapter.kafka.consumer

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.kafka.client.consumer.KafkaConsumer


abstract class KafkaConsumerVerticle(
    private val kafkaConfig: KafkaConsumerConfig
) : AbstractVerticle() {
    private val log = LoggerFactory.getLogger(this::class.java)

    abstract fun loadHandlers(): EventRouterHandler

    override fun start(startPromise: Promise<Void>) {
        val consumer: KafkaConsumer<String, String> = KafkaConsumer.create(vertx, kafkaConfig.consumerConfig())
        consumer
            .handler(loadHandlers())
            .subscribe(kafkaConfig.topic())
            .onFailure { log.error("Subscription failed", it) }
            .onSuccess { log.info("subscribed") }
    }
}