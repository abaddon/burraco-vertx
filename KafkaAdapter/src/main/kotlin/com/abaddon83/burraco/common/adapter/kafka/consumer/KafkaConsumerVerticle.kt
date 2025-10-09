package com.abaddon83.burraco.common.adapter.kafka.consumer

import io.github.abaddon.kcqrs.core.helpers.KcqrsLoggerFactory.log
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.kafka.client.consumer.KafkaConsumer
import io.vertx.kafka.client.consumer.KafkaConsumerRecord


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
            .pause() // Start paused to control consumption
            .subscribe(kafkaConfig.topic())
            .onFailure { log.error(" ${kafkaConfig.topic()} subscriptions failed", it) }
            .onSuccess {
                log.info("${kafkaConfig.topic()} subscribed")
                consumer.resume() // Resume to start consuming first message
            }
        super.start(startPromise)
    }

    override fun stop(stopPromise: Promise<Void>?) {
        log.info("KafkaConsumerVerticle stopping")
        consumer.unsubscribe()
            .onComplete { super.stop(stopPromise) }
    }

    private fun processRecord(record: KafkaConsumerRecord<String, String>, eventRouterHandler: EventRouterHandler) {
        // Pause consumer to prevent consuming next message until current one is processed and committed
        consumer.pause()

        vertx.executeBlocking<Unit>({ promise ->
            runCatching {
                // Process the record on worker thread
                eventRouterHandler.handle(record)
                promise.complete()
            }.onFailure {
                promise.fail(it)
            }
        }, true) { result -> // ordered = true to maintain order
            if (result.succeeded()) {
                // Commit only after successful processing
                consumer.commit()
                    .onSuccess {
                        log.debug("Offset committed for ${record.topic()}:${record.partition()}:${record.offset()}")
                        // Resume consumer to process next message only after commit
                        consumer.resume()
                    }
                    .onFailure { error ->
                        log.error("Failed to commit offset", error)
                        // Resume even on commit failure to avoid getting stuck
                        consumer.resume()
                    }
            } else {
                log.error(
                    "Error processing record ${record.topic()}:${record.partition()}:${record.offset()}",
                    result.cause()
                )
                // Resume consumer even on processing failure
                consumer.resume()
            }
        }
    }
}