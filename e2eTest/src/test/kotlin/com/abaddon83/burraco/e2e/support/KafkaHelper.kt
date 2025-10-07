package com.abaddon83.burraco.e2e.support

import com.abaddon83.burraco.e2e.infrastructure.ServiceEndpoints
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import java.time.Duration
import java.util.*

/**
 * Kafka helper for consuming and verifying events.
 * Can be used to assert that expected events were published.
 */
class KafkaHelper {
    private val consumer: KafkaConsumer<String, String>

    init {
        val props = Properties().apply {
            put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, ServiceEndpoints.get().kafkaBroker)
            put(ConsumerConfig.GROUP_ID_CONFIG, "e2e-test-group-${UUID.randomUUID()}")
            put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
            put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
            put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
            put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
        }
        consumer = KafkaConsumer(props)
    }

    /**
     * Subscribe to a topic
     */
    fun subscribe(topic: String) {
        consumer.subscribe(listOf(topic))
    }

    /**
     * Poll for messages with timeout
     */
    fun pollMessages(timeout: Duration = Duration.ofSeconds(5)): List<String> {
        val records = consumer.poll(timeout)
        return records.map { it.value() }
    }

    /**
     * Close the consumer
     */
    fun close() {
        consumer.close()
    }

    companion object {
        fun create(): KafkaHelper {
            return KafkaHelper()
        }
    }
}
