package com.abaddon83.vertx.burraco.game.adapters.eventBrokerConsumerAdapter.config

data class KafkaConsumerConfig(
    val bootstrapServer: String,
    val keyDeserializer: String,
    val valueDeserializer: String,
    val groupId: String,
    val autoOffsetReset: String,
    val enableAutoCommit: String

) {
    constructor(): this(
        bootstrapServer = "localhost:50560",
        keyDeserializer = "org.apache.kafka.common.serialization.StringDeserializer",
        valueDeserializer = "org.apache.kafka.common.serialization.StringDeserializer",
        groupId = "dealer",
        autoOffsetReset = "earliest",
        enableAutoCommit = "false"
    )

    fun consumerConfig(): Map<String, String> = mapOf(
        "bootstrap.servers" to bootstrapServer,
        "key.deserializer" to keyDeserializer,
        "value.deserializer" to valueDeserializer,
        "group.id" to groupId,
        "auto.offset.reset" to autoOffsetReset,
        "enable.auto.commit" to enableAutoCommit

    )

}
