package com.abaddon83.burraco.common.adapter.kafka.consumer
import org.apache.kafka.clients.consumer.ConsumerConfig

data class KafkaConsumerConfig(
    private val properties: Map<String, String>,
    private val topic: String
){
    init {
        properties.keys.forEach { key ->
            validatePropertyKey(key)
        }
    }

    fun withTopic(topic: String): KafkaConsumerConfig =
        this.copy(topic = topic)

    fun withProperty(key: String, value: String): KafkaConsumerConfig {
        validatePropertyKey(key)
        return this.copy(properties = properties.plus(Pair(key,value)))
    }


    fun consumerConfig(): Map<String, String> = properties

    fun topic(): String = topic;

    private fun validatePropertyKey(key: String){
        check(ConsumerConfig.configNames().contains(key)){ "Key $key is not valid"}
    }

    companion object{
        fun empty() : KafkaConsumerConfig = KafkaConsumerConfig(mapOf(),"")
    }
}
