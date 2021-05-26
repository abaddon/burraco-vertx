package com.abaddon83.vertx.burraco.game.adapters.eventBrokerConsumerAdapter

import com.abaddon83.burraco.common.events.*
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.utils.functionals.Invalid
import com.abaddon83.utils.functionals.Valid
import com.abaddon83.vertx.burraco.game.adapters.eventBrokerConsumerAdapter.config.KafkaConsumerConfig
import com.abaddon83.vertx.burraco.game.commands.*
import com.abaddon83.vertx.burraco.game.models.BurracoGame
import com.abaddon83.vertx.burraco.game.ports.EventBrokerConsumerPort
import com.abaddon83.vertx.burraco.game.ports.EventBrokerProducerPort
import com.abaddon83.vertx.burraco.game.ports.EventStorePort
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.kafka.client.consumer.KafkaConsumer
import org.slf4j.LoggerFactory
import java.time.Duration


class KafkaEventBrokerConsumerAdapter(private val kafkaConfig: KafkaConsumerConfig, private val eventStore: EventStorePort, private val eventBrokerProducerPort: EventBrokerProducerPort<String, String>) : EventBrokerConsumerPort,
    AbstractVerticle() {

    private val log = LoggerFactory.getLogger(this::class.java)
    private val TOPIC: String = "dealer"
    private val pollingDuration: Long = 100L
    private val commandHandler = CommandHandler(eventStore, eventBrokerProducerPort);

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
                                    is CardAssignedToPlayer -> commandHandler.handle(ApplyCardToPlayer(ev.identity, ev.player, ev.card))
                                    is CardAssignedToPlayerDeck -> commandHandler.handle(ApplyCardToPlayerDeck(ev.identity, ev.playerDeckId, ev.card))
                                    is CardAssignedToDiscardDeck -> commandHandler.handle(ApplyCardToDiscardDeck(ev.identity, ev.card))
                                    is CardAssignedToDeck -> commandHandler.handle(ApplyCardToDeck(ev.identity, ev.card))
                                    else -> Valid(DomainResult(listOf(), BurracoGame.create(GameIdentity.create())))
                                }
                                result
                            }
                            if(results.count{ it is Invalid<*> } == 0){
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