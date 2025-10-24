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

    /**
     * Wait for a specific event to appear in Kafka topic
     * @param topic The Kafka topic to monitor
     * @param eventTypePredicate Predicate to match the event type (e.g., checks if JSON contains "GameCreated")
     * @param maxWaitSeconds Maximum time to wait in seconds (default 3)
     * @return true if event found, false if timeout
     */
    fun waitForEvent(topic: String, eventTypePredicate: (String) -> Boolean, maxWaitSeconds: Long = 3): Boolean {
        subscribe(topic)

        // Ensure consumer is assigned to partitions before seeking
        consumer.poll(Duration.ofMillis(100))

        // Seek to beginning to ensure we read all messages
        consumer.seekToBeginning(consumer.assignment())

        val startTime = System.currentTimeMillis()
        val maxWaitMillis = maxWaitSeconds * 1000

        while (System.currentTimeMillis() - startTime < maxWaitMillis) {
            val messages = pollMessages(Duration.ofMillis(500))
            if (messages.any(eventTypePredicate)) {
                return true
            }
        }
        return false
    }

    companion object {
        fun create(): KafkaHelper {
            return KafkaHelper()
        }

        /**
         * Verify that a GameCreated event for a specific game ID was published to Kafka
         * @param gameId The game ID to verify
         * @param maxWaitSeconds Maximum time to wait (default 3 seconds)
         * @return true if event found, false otherwise
         */
        fun verifyGameCreatedEvent(gameId: String, maxWaitSeconds: Long = 3): Boolean {
            val helper = create()
            try {
                // First check if any event with this gameId exists
                val found = helper.waitForEvent("game", { message ->
                    message.contains(gameId) && (
                        message.contains("\"eventName\":\"GameCreated\"") ||
                        message.contains("GameCreated")
                    )
                }, maxWaitSeconds)

                if (!found) {
                    // Debug: print all messages to see what we're getting
                    println("DEBUG: No GameCreated event found for gameId=$gameId. Checking all messages...")
                }

                return found
            } finally {
                helper.close()
            }
        }
    }
}
