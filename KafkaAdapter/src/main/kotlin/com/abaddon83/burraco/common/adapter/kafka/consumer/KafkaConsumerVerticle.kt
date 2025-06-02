package com.abaddon83.burraco.common.adapter.kafka.consumer

import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.kafka.client.consumer.KafkaConsumer
import io.vertx.kafka.client.consumer.KafkaConsumerRecord
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.job


abstract class KafkaConsumerVerticle(
    private val kafkaConfig: KafkaConsumerConfig
) : AbstractVerticle() {
    private lateinit var consumer: KafkaConsumer<String, String>
    abstract fun loadHandlers(): EventRouterHandler

    override fun start(startPromise: Promise<Void>) {
        log.info("KafkaConsumerVerticle starting")

        val eventRouterHandler: EventRouterHandler = loadHandlers()

        consumer = KafkaConsumer.create(vertx, kafkaConfig.consumerConfig())
        consumer
            .handler { record -> processRecord(record, eventRouterHandler) }
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

    private fun processRecord(record: KafkaConsumerRecord<String, String>, eventRouterHandler: EventRouterHandler) {
        runCatching {
            // Process the record
            eventRouterHandler.handle(record)


        }.onFailure {
            log.error("Error processing record ${record.topic()}:${record.partition()}:${record.offset()}", it)
            // Handle the error, e.g., retry logic or logging
        }.onSuccess {
            // Commit only after successful processing
            consumer.commit()
                .onSuccess { log.debug("Offset committed for ${record.topic()}:${record.partition()}:${record.offset()}") }
                .onFailure { error ->
                    log.error("Failed to commit offset", error)
                    // Implement retry logic or error handling
                }
        }
    }
}