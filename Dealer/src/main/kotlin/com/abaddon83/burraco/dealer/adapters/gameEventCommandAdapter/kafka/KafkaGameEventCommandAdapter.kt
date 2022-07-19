package com.abaddon83.burraco.dealer.adapters.gameEventCommandAdapter.kafka

import com.abaddon83.burraco.dealer.models.Dealer
import com.abaddon83.burraco.dealer.models.DealerIdentity
import com.abaddon83.burraco.dealer.models.GameIdentity
import com.abaddon83.burraco.dealer.models.PlayerIdentity
import com.abaddon83.burraco.dealer.ports.*
import com.abaddon83.burraco.dealer.services.DealerService
import com.abaddon83.burraco.dealer.services.DealerServiceResult
import io.vertx.core.*
import io.vertx.kafka.client.consumer.KafkaConsumer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration

class KafkaGameEventCommandAdapter(
    private val kafkaConfig: KafkaConsumerConfig,
    override val dealerService: DealerService
) : CommandPort, AbstractVerticle() {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)
    private val topic: String = kafkaConfig.topic
    private val pollingDuration: Long = kafkaConfig.pollingDurationMs
    private val consumerConfig: Map<String, String> = kafkaConfig.consumerConfig()

    override fun start(startPromise: Promise<Void>) {
        val consumer: KafkaConsumer<String, String> = KafkaConsumer.create(vertx, consumerConfig);
        consumer
            .subscribe(topic)
            .onFailure {
                log.error("Subscription failed", it)
            }
            .onSuccess {
                vertx.setPeriodic(500) { timerId ->
                    consumer
                        .poll(Duration.ofMillis(pollingDuration))
                        .onSuccess { records ->
                            val done: Promise<List<CommandPortResult<DomainError, DomainResult>>> = Promise.promise()
                            GlobalScope.async {
                                val results = records.records().map { record ->
                                    val kafkaEvent = KafkaGameEvent.createFrom(record)
                                    executeCommand(kafkaEvent)
                                }
                                done.complete(results)
                            }
                            done.future().onSuccess {
                                val countInValid = it.filterIsInstance<CommandPortResult.Invalid<DomainError>>().count()
                                if (countInValid == 0) {
                                    consumer.commit()
                                        .onSuccess { v -> log.info("Offset Committed") }
                                        .onFailure { cause -> log.error("Commit failed", cause) }
                                }
                            }.onFailure {
                                log.error("Commit failed", it)
                            }
                        }.onFailure { cause ->
                            log.error("Polling error.", cause)
                        }
                }
            }
    }

    private suspend fun executeCommand(kafkaGameEvent: KafkaGameEvent): CommandPortResult<DomainError, DomainResult> =
        when (kafkaGameEvent.eventName) {
            "GameInitialised" -> {
                val dealerIdentity: DealerIdentity = TODO()
                val gameIdentity: GameIdentity = TODO()
                val players: List<PlayerIdentity> = TODO()
                createDeck(dealerIdentity, gameIdentity, players)
            }
            else -> CommandPortResult.Valid(DomainResult(listOf(), Dealer.empty()))
        }

    override suspend fun createDeck(
        dealerIdentity: DealerIdentity,
        gameIdentity: GameIdentity,
        players: List<PlayerIdentity>
    ): CommandPortResult<DomainError, DomainResult> =
        when (val result = dealerService.dealCards(dealerIdentity, gameIdentity, players)) {
            is DealerServiceResult.Invalid -> CommandPortResult.Invalid(result.err)
            is DealerServiceResult.Valid -> CommandPortResult.Valid(result.value)
        }
}