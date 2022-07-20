package com.abaddon83.burraco.game.adapters.gameEventPublisher.kafka

import org.apache.kafka.clients.producer.ProducerConfig
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class KafkaProducerConfigTest {

    @Test
    fun `Given a valid Kafka parameter, when add it, then it's on the config property`() {
        val property = Pair(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")

        val kafkaProducerConfig = KafkaProducerConfig.empty().withProperty(property.first, property.second)

        assertTrue(kafkaProducerConfig.producerConfig().containsKey(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG))
        assertEquals(property.second, kafkaProducerConfig.producerConfig().get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG))
        assertEquals(1,kafkaProducerConfig.producerConfig().entries.size)
    }

    @Test
    fun `Given a not valid Kafka parameter, when add it, then exception`() {
        val property = Pair("fake_param", "localhost:9092")

        assertThrows<IllegalStateException>(){
            KafkaProducerConfig.empty().withProperty(property.first, property.second)
        }
    }

    @Test
    fun `Given a topic name, when set it, then topic is in the config`() {
        val topicName = "topic_1"

        val kafkaProducerConfig = KafkaProducerConfig.empty().withTopic(topicName)

        assertEquals(topicName, kafkaProducerConfig.topic())
    }
}