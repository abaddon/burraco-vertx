package com.abaddon83.burraco.helper

import com.abaddon83.burraco.common.models.event.game.GameEvent
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.kafka.admin.KafkaAdminClient
import io.vertx.kafka.admin.NewTopic
import io.vertx.kafka.client.consumer.KafkaConsumer
import io.vertx.kafka.client.consumer.KafkaConsumerRecord
import io.vertx.kafka.client.producer.KafkaProducer
import io.vertx.kafka.client.producer.KafkaProducerRecord
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.runBlocking
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.junit.jupiter.api.extension.ExtendWith
import org.testcontainers.kafka.KafkaContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.util.UUID

@Testcontainers
@ExtendWith(VertxExtension::class)
abstract class KafkaContainerTest {

    companion object {
        internal val vertx: Vertx = Vertx.vertx();
    }

    @Container
    internal val kafka: KafkaContainer = KafkaContainer(DockerImageName.parse("apache/kafka"))


    private fun producerConfig(): Map<String, String> = mapOf<String, String>(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to kafka.bootstrapServers,
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to "org.apache.kafka.common.serialization.StringSerializer",
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to "org.apache.kafka.common.serialization.StringSerializer",
        ProducerConfig.ACKS_CONFIG to "1"
    )

    private fun consumerConfig(): Map<String, String> = mapOf<String, String>(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafka.bootstrapServers,
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to "org.apache.kafka.common.serialization.StringDeserializer",
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to "org.apache.kafka.common.serialization.StringDeserializer",
        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
        ConsumerConfig.GROUP_ID_CONFIG to UUID.randomUUID().toString(),
        ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to "true"
    )

    private fun adminConfig(): Map<String, String> = mapOf<String, String>(
        AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG to kafka.bootstrapServers
    )

    private lateinit var producer: KafkaProducer<String, String>


    internal fun initConsumer(
        topic: String,
        handler: Handler<KafkaConsumerRecord<String, String>>
    ): KafkaConsumer<String, String> {
        createTopic(topic)
        val consumer: KafkaConsumer<String, String> = KafkaConsumer.create(vertx, consumerConfig())
        consumer.handler(handler)
        consumer.subscribe(topic)
        return consumer
    }

    private fun createTopic(topic: String) = runBlocking {
        KafkaAdminClient.create(vertx, adminConfig()).createTopics(listOf(NewTopic(topic, 1, 1)))
            .onSuccess { println("topic created") }
            .onFailure { println("topic not created, cause: ${it.message}") }.await()
    }


    private fun getProducer(): KafkaProducer<String, String> {
        if (producer == null) {
            val producer: KafkaProducer<String, String> = KafkaProducer.create(vertx, producerConfig());
            this.producer = producer
        }
        return this.producer
    }

    private fun publishMessage(topic: String, key: String, value: String) {
        getProducer().write(KafkaProducerRecord.create(topic, key, value))
    }

    internal fun <T : GameEvent> publishMessage(topic: String, key: String, value: T) {
        publishMessage(topic, key, value.toJson())
    }

//    internal fun readMessage() {
//
//    }


}