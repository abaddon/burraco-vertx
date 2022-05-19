//package com.abaddon83.burraco.dealer.adapters.eventBrokerConsumerAdapter
//
//
//import com.abaddon83.burraco.common.events.ExtendEvent
//import com.abaddon83.burraco.common.events.GameInitialised
//import com.abaddon83.burraco.common.models.identities.GameIdentity
//import com.abaddon83.burraco.common.models.identities.PlayerIdentity
//import com.abaddon83.utils.functionals.Invalid
//import com.abaddon83.utils.functionals.Valid
//import com.abaddon83.burraco.dealer.adapters.eventBrokerConsumerAdapter.config.KafkaConsumerConfig
//import com.abaddon83.burraco.dealer.commands.CmdResult
//import com.abaddon83.burraco.dealer.commands.CommandHandler
//import com.abaddon83.burraco.dealer.commands.DealCards
//import com.abaddon83.burraco.dealer.commands.DomainResult
//import com.abaddon83.burraco.dealer.ports.GamePort
//import com.abaddon83.burraco.dealer.ports.EventBrokerProducerPort
//import io.vertx.core.AbstractVerticle
//import io.vertx.core.Promise
//import io.vertx.kafka.client.consumer.KafkaConsumer
//import org.slf4j.LoggerFactory
//import java.time.Duration
//
//
//class KafkaConsumerGameAdapter(private val kafkaConfig: KafkaConsumerConfig, private val eventBrokerProducerPort: EventBrokerProducerPort<String, String>) : GamePort, AbstractVerticle() {
//
//    private val log = LoggerFactory.getLogger(this::class.java)
//    private val TOPIC: String = "game"
//    private val pollingDuration: Long = 100L
//
//    override val commandHandler: CommandHandler
//        get() = CommandHandler(eventBrokerProducerPort);
//
//    override fun start(startPromise: Promise<Void>) {
//        val consumer: KafkaConsumer<String, String> = KafkaConsumer.create(vertx, kafkaConfig.consumerConfig());
//        consumer
//            .subscribe(TOPIC)
//            .onFailure {
//                log.error("Subscription failed", it)
//            }
//            .onSuccess {
//                vertx.setPeriodic(500) { timerId ->
//                    consumer
//                        .poll(Duration.ofMillis(pollingDuration))
//                        .onSuccess { records ->
//                            val results = records.records().map { record  ->
//                                val event = ExtendEvent(record.value())
//                                val result = when(val ev = event.toEvent()){
//                                    is GameInitialised -> dealCards(ev.identity,ev.players)
//                                    else -> Valid(DomainResult(GameIdentity.create(), listOf()))
//                                }
//                                result
//                            }
//                            if(results.count{ it is Invalid } == 0){
//                                consumer.commit()
//                                    //.onSuccess { v -> log.info("Offset Committed") }
//                                    .onFailure { cause ->  log.error("Commit failed",cause)}
//                            }
//                        }.onFailure { cause ->
//                            log.error("Polling error.", cause)
//                        }
//                }
//            }
//    }
//
//    override fun dealCards(identity: GameIdentity, players: List<PlayerIdentity>) : CmdResult {
//        return commandHandler.handle(DealCards(identity,players))
//    }
//
//}