package com.abaddon83.burraco.game.adapters.gameEventPublisher.kafka
import org.apache.kafka.clients.producer.ProducerConfig

data class KafkaProducerConfig(
    private val properties: Map<String, String>,
    private val topic: String
){
    init {
        properties.keys.forEach { key ->
            validatePropertyKey(key)
        }
    }

    fun withTopic(topic: String): KafkaProducerConfig =
        this.copy(topic = topic)

    fun withProperty(key: String, value: String): KafkaProducerConfig {
        validatePropertyKey(key)
        return this.copy(properties = properties.plus(Pair(key,value)))
    }


    fun producerConfig(): Map<String, String> = properties

    fun topic(): String = topic;

    private fun validatePropertyKey(key: String){
        check(ProducerConfig.configNames().contains(key)){ "Key $key is not valid"}
    }

    companion object{
        fun empty() : KafkaProducerConfig = KafkaProducerConfig(mapOf(),"")
    }
}

//data class KafkaProducerConfig(
//    val bootstrapServer: String,
//    val keySerializer: String,
//    val valueSerializer: String,
//    val acks: String
//
//) {
//
//    constructor(): this(
//        bootstrapServer = "localhost:62666",
//        keySerializer = "org.apache.kafka.common.serialization.StringSerializer",
//        valueSerializer = "org.apache.kafka.common.serialization.StringSerializer",
//        acks = "-1"
//    )
//
//    fun producerConfig(): Map<String, String> = mapOf(
//        "bootstrap.servers"  = "localhost:62666",
//        "key.serializer" = "org.apache.kafka.common.serialization.StringSerializer"
//        "value.serializer" = "org.apache.kafka.common.serialization.StringSerializer"
//        "acks" = "-1"
//    )
//
//}
