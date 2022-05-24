package com.abaddon83.burraco.dealer.adapters.eventAdapters.kafka

import org.apache.kafka.clients.producer.ProducerConfig

data class KafkaProducerConfig(
    val topic: String,
    val bootstrapServer: String,
    val keySerializer: String = "org.apache.kafka.common.serialization.StringSerializer",
    val valueSerializer: String = "org.apache.kafka.common.serialization.StringSerializer",
    val acks: String = "-1"

) {

    constructor(): this(
        bootstrapServer = "localhost:50560",
        keySerializer = "org.apache.kafka.common.serialization.StringSerializer",
        valueSerializer = "org.apache.kafka.common.serialization.StringSerializer",
        acks = "-1",
        topic="test"
    )

    fun producerConfig(): Map<String, String> = mapOf(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServer,
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to keySerializer,
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to valueSerializer,
        ProducerConfig.ACKS_CONFIG to acks
    )

}
