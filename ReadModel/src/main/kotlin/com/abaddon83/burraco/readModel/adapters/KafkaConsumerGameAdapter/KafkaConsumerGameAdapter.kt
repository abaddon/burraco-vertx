package com.abaddon83.burraco.readModel.adapters.KafkaConsumerGameAdapter


import com.abaddon83.burraco.common.events.*
import com.abaddon83.burraco.readModel.adapters.KafkaConsumerGameAdapter.config.KafkaConsumerConfig
import com.abaddon83.burraco.readModel.commands.*
import com.abaddon83.burraco.readModel.ports.GamePort
import com.abaddon83.burraco.readModel.ports.RepositoryPort
import com.abaddon83.burraco.readModel.projections.BurracoGame
import com.abaddon83.burraco.readModel.projections.GamePlayer
import io.vertx.core.AbstractVerticle
import io.vertx.core.CompositeFuture
import io.vertx.core.Promise
import io.vertx.kafka.client.consumer.KafkaConsumer
import org.slf4j.LoggerFactory
import java.time.Duration


class KafkaConsumerGameAdapter(private val kafkaConfig: KafkaConsumerConfig, override val repository: RepositoryPort) : GamePort, AbstractVerticle() {

    private val log = LoggerFactory.getLogger(this::class.java)
    private val TOPIC: String = "game"
    private val pollingDuration: Long = 100L

    val commandHandler: CommandHandler
        get() = CommandHandler(repository);
    
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
                                val event = ExtendEvent(record.value()).toEvent()
                                val result = when(event){
                                    is BurracoGameCreated -> applyEvent(ApplyEventBurracoGameCreated(event))
                                    is PlayerAdded -> applyEvent(ApplyEventPlayerAdded(event))
                                    is GameInitialised -> applyEvent(ApplyEventGameInitialised(event))
                                    is CardAssignedToPlayer -> applyEvent(ApplyEventCardAssignedToPlayer(event))
                                    is CardAssignedToPlayerDeck -> applyEvent(ApplyEventCardAssignedToPlayerDeck(event))
                                    is CardAssignedToDeck -> applyEvent(ApplyEventCardAssignedToDeck(event))
                                    is CardAssignedToDiscardDeck -> applyEvent(ApplyEventCardAssignedToDiscardDeck(event))
                                    is GameStarted -> applyEvent(ApplyEventGameStarted(event))


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
                        }.onFailure { cause ->
                            log.error("Polling error.", cause)
                        }
                }
            }
    }

    override fun applyEvent(cmd: CommandImpl): Promise<CmdResult>{
        return commandHandler.handle(cmd)
    }

}