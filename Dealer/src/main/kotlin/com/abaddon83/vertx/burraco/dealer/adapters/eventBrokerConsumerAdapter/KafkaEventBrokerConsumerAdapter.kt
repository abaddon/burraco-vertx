package com.abaddon83.vertx.burraco.dealer.adapters.eventBrokerConsumerAdapter


import com.abaddon83.burraco.common.events.ExtendEvent
import com.abaddon83.burraco.common.events.GameInitialised
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.utils.functionals.Invalid
import com.abaddon83.utils.functionals.Valid
import com.abaddon83.vertx.burraco.dealer.adapters.eventBrokerConsumerAdapter.config.KafkaConsumerConfig
import com.abaddon83.vertx.burraco.dealer.commands.CommandHandler
import com.abaddon83.vertx.burraco.dealer.commands.DealCards
import com.abaddon83.vertx.burraco.dealer.commands.DomainResult
import com.abaddon83.vertx.burraco.dealer.ports.EventBrokerConsumerPort
import com.abaddon83.vertx.burraco.dealer.ports.EventBrokerProducerPort
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.kafka.client.consumer.KafkaConsumer
import org.slf4j.LoggerFactory
import java.time.Duration


class KafkaEventBrokerConsumerAdapter(private val kafkaConfig: KafkaConsumerConfig, private val eventBrokerProducerPort: EventBrokerProducerPort<String,String>) : EventBrokerConsumerPort,
    AbstractVerticle() {

    private val log = LoggerFactory.getLogger(this::class.java)
    private val TOPIC: String = "game"
    private val pollingDuration: Long = 100L
    private val commandHandler = CommandHandler(eventBrokerProducerPort);

    override fun start(startPromise: Promise<Void>) {
        val consumer: KafkaConsumer<String, String> = KafkaConsumer.create(vertx, kafkaConfig.consumerConfig());
        consumer
            .subscribe(TOPIC)
            .onFailure {
                log.error("Subscription failed", it)
            }
            .onSuccess {
                vertx.setPeriodic(500) { timerId ->
                    consumer
                        .poll(Duration.ofMillis(pollingDuration))
                        .onSuccess { records ->
                            val results = records.records().map { record  ->
                                val event = ExtendEvent(record.value())
                                val result = when(val ev = event.toEvent()){
                                    is GameInitialised -> commandHandler.handle(DealCards(ev.identity,ev.players))
                                    else -> Valid(DomainResult(GameIdentity.create(), listOf()))
                                }
                                result
                            }
                            if(results.count{ it is Invalid } == 0){
                                consumer.commit()
                                    //.onSuccess { v -> log.info("Offset Committed") }
                                    .onFailure { cause ->  log.error("Commit failed",cause)}
                            }
                        }.onFailure { cause ->
                            log.error("Polling error.", cause)
                        }
                }
            }


    }

}