//package com.abaddon83.burraco.dealer.adapters.eventAdapters.kafka
//
//import com.abaddon83.burraco.dealer.ports.EventsPort
//import com.abaddon83.burraco.dealer.ports.EventsPortResult
//import com.abaddon83.burraco.dealer.ports.PublishResult
//import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
//import io.vertx.core.Vertx
//import io.vertx.kafka.client.producer.KafkaProducer
//import io.vertx.kafka.client.producer.KafkaProducerRecord
//import io.vertx.kotlin.coroutines.await
//import org.slf4j.Logger
//import org.slf4j.LoggerFactory
//
//class KafkaEventAdapter(
//    private val vertx: Vertx,
//    private val kafkaConfig: KafkaProducerConfig
//) : EventsPort {
//    private val log: Logger = LoggerFactory.getLogger(this::class.java)
//    private val producer: KafkaProducer<String, String> = KafkaProducer.create(vertx, kafkaConfig.producerConfig());
//
//    override suspend fun publish(event: IDomainEvent): EventsPortResult<Exception, PublishResult> {
//        val kafkaEvent = KafkaEvent(event)
//        val kafkaProducerRecord = KafkaProducerRecord.create(
//            kafkaConfig.topic,
//            kafkaEvent.key,
//            kafkaEvent.toJsonString()
//        )
//        return try {
//            val sendResult = producer.send(kafkaProducerRecord).await()
//            val mapValues = mapOf(
//                Pair("offset", sendResult.offset.toString()),
//                Pair("partition", "${sendResult.topic}:${sendResult.partition}")
//            )
//            EventsPortResult.Valid(PublishResult(mapValues))
//        } catch (e: Exception) {
//            EventsPortResult.Invalid(e)
//        }
//    }
//
//
//    override suspend fun publish(events: List<IDomainEvent>): List<EventsPortResult<Exception, PublishResult>> =
//        events.map {
//            publish(it)
//        }
//}