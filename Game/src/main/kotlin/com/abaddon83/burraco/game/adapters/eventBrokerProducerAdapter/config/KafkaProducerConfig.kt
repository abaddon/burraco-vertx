package com.abaddon83.burraco.game.adapters.eventBrokerProducerAdapter.config

data class KafkaProducerConfig(
    val bootstrapServer: String,
    val keySerializer: String,
    val valueSerializer: String,
    val acks: String

) {

    constructor(): this(
        bootstrapServer = "localhost:50560",
        keySerializer = "org.apache.kafka.common.serialization.StringSerializer",
        valueSerializer = "org.apache.kafka.common.serialization.StringSerializer",
        acks = "-1"
    )

    fun producerConfig(): Map<String, String> = mapOf(
        "bootstrap.servers" to bootstrapServer,
        "key.serializer" to keySerializer,
        "value.serializer" to valueSerializer,
        "acks" to acks,
    )

}
