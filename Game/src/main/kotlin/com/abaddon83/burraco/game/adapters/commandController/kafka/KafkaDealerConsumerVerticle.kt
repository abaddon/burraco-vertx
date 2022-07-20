package com.abaddon83.burraco.game.adapters.commandController.kafka

import com.abaddon83.burraco.game.adapters.commandController.kafka.config.KafkaConsumerConfig
import com.abaddon83.burraco.game.adapters.commandController.kafka.handlers.AddCardDeckHandler
import com.abaddon83.burraco.game.ports.CommandControllerPort
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.kafka.client.consumer.KafkaConsumer


class KafkaDealerConsumerVerticle(
    private val kafkaConfig: KafkaConsumerConfig,
    private val controllerAdapter: CommandControllerPort
) : AbstractVerticle() {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun start(startPromise: Promise<Void>) {
        val eventRouterHandler = EventRouterHandler()
            .addHandler("DeckCardDealt", AddCardDeckHandler(controllerAdapter))


        val consumer: KafkaConsumer<String, String> = KafkaConsumer.create(vertx, kafkaConfig.consumerConfig())
        consumer
            .handler(eventRouterHandler)
            .subscribe(kafkaConfig.topic())
            .onFailure { log.error("Subscription failed", it) }
            .onSuccess { log.info("subscribed") }
    }
}