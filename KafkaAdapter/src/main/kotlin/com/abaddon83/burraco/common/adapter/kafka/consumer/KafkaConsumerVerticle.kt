package com.abaddon83.burraco.common.adapter.kafka.consumer

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.kafka.client.consumer.KafkaConsumer


abstract class KafkaConsumerVerticle(
    private val kafkaConfig: KafkaConsumerConfig
) : AbstractVerticle() {
    private val log = LoggerFactory.getLogger(this::class.java)
    lateinit var consumer: KafkaConsumer<String, String>

    abstract fun loadHandlers(): EventRouterHandler

    override fun start(startPromise: Promise<Void>) {
        log.info("KafkaConsumerVerticle starting")
        consumer = KafkaConsumer.create(vertx, kafkaConfig.consumerConfig())
        consumer
            .handler(loadHandlers())
            .subscribe(kafkaConfig.topic())
            .onFailure { log.error(" ${kafkaConfig.topic()} subscriptions failed", it) }
            .onSuccess { log.info("${kafkaConfig.topic()} subscribed") }
        super.start(startPromise);
    }

    override fun stop(stopPromise: Promise<Void>?) {
        log.info("KafkaConsumerVerticle stopping")
        consumer.unsubscribe()
            .onComplete { super.stop(stopPromise) }
    }
}