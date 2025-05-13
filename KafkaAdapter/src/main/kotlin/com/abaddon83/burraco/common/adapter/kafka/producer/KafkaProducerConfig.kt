package com.abaddon83.burraco.common.adapter.kafka.producer

import kotlinx.serialization.Serializable
import org.apache.kafka.clients.producer.ProducerConfig

@Serializable
data class KafkaProducerConfig(
    private val properties: Map<String, String>,
    private val topic: String
) {
    init {
        properties.keys.forEach { key ->
            validatePropertyKey(key)
        }
    }

    fun withTopic(topic: String): KafkaProducerConfig =
        this.copy(topic = topic)

    fun withProperty(key: String, value: String): KafkaProducerConfig {
        validatePropertyKey(key)
        return this.copy(properties = properties.plus(Pair(key, value)))
    }


    fun producerConfig(): Map<String, String> = properties

    fun topic(): String = topic;

    private fun validatePropertyKey(key: String) {
        check(ProducerConfig.configNames().contains(key)) { "Key $key is not valid" }
    }

    companion object {
        fun empty(): KafkaProducerConfig = KafkaProducerConfig(mapOf(), "")
    }
}
