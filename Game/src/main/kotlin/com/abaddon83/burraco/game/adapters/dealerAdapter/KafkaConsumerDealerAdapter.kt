package com.abaddon83.burraco.game.adapters.dealerAdapter

import com.abaddon83.burraco.common.events.*
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.burraco.game.adapters.dealerAdapter.config.KafkaDealerConsumerConfig
import com.abaddon83.burraco.game.commands.*
import com.abaddon83.burraco.game.ports.*
import io.vertx.core.AbstractVerticle
import io.vertx.core.CompositeFuture
import io.vertx.core.Promise
import io.vertx.kafka.client.consumer.KafkaConsumer
import org.slf4j.LoggerFactory
import java.time.Duration

class KafkaConsumerDealerAdapter(
    private val kafkaConfig: KafkaDealerConsumerConfig,
    override val eventStore: EventStorePort,
    private val gameEventsBrokerProducer: GameEventsBrokerProducerPort<String, String>
) : CommandControllerPort,
    AbstractVerticle() {
    val commandHandler: CommandHandler
        get() = CommandHandler(eventStore,gameEventsBrokerProducer)

    private val log = LoggerFactory.getLogger(this::class.java)
    private val TOPIC: String = "dealer"
    private val pollingDuration: Long = 100L

    override fun start(startPromise: Promise<Void>) {
        val consumer: KafkaConsumer<String, String> = KafkaConsumer.create(vertx, kafkaConfig.consumerConfig());
        consumer
            .subscribe(TOPIC)
            .onFailure { log.error("Subscription failed", it) }
            .onSuccess {
                vertx.setPeriodic(500) { timerId ->
                    consumer
                        .poll(Duration.ofMillis(pollingDuration))
                        .onFailure { cause -> log.error("Polling error.", cause) }
                        .onSuccess { records ->
                            val results = records.records().map { record ->
                                val event = ExtendEvent(record.value())
                                val result = when (val ev = event.toEvent()) {
                                    is CardAssignedToPlayer -> applyCardToPlayer(ev.identity, ev.player, ev.card)
                                    is CardAssignedToPlayerDeck -> applyCardToPlayerDeck(ev.identity, ev.playerDeckId, ev.card)
                                    is CardAssignedToDiscardDeck -> applyCardToDiscardDeck(ev.identity, ev.card)
                                    is CardAssignedToDeck -> applyCardToDeck(ev.identity, ev.card)
                                    else -> Promise.promise()
                                }
                                result.future()
                            }
                            CompositeFuture.all(results).onComplete {
                                if (it.succeeded()) {
                                    consumer.commit()
                                        .onFailure { cause -> log.error("Commit failed", cause) }
                                }
                            }
                        }
                }
            }
    }

    override fun applyCardToPlayer(identity: GameIdentity, playerIdentity: PlayerIdentity, card: Card): Promise<CmdResult> {
        return commandHandler.handle(ApplyCardToPlayer(identity, playerIdentity, card))
    }

    override fun applyCardToPlayerDeck(identity: GameIdentity, playerDeckId: Int, card: Card): Promise<CmdResult> {
        return commandHandler.handle(ApplyCardToPlayerDeck(identity, playerDeckId, card))
    }

    override fun applyCardToDiscardDeck(identity: GameIdentity, card: Card): Promise<CmdResult> {
        return commandHandler.handle(ApplyCardToDiscardDeck(identity, card))
    }

    override fun applyCardToDeck(identity: GameIdentity, card: Card): Promise<CmdResult> {
        return commandHandler.handle(ApplyCardToDeck(identity, card))
    }

}