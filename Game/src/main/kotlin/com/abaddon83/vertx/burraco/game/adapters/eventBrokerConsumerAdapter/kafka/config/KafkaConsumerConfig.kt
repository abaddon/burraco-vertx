package com.abaddon83.vertx.burraco.game.adapters.eventBrokerConsumerAdapter.kafka.config

data class KafkaConsumerConfig(
    val bootstrapServer: String,
    val keyDeserializer: String,
    val valueDeserializer: String,
    val groupId: String,
    val autoOffsetReset: String,
    val enableAutoCommit: String

) {

    fun consumerConfig(): Map<String, String> = mapOf(
        "bootstrap.servers" to bootstrapServer,
        "key.deserializer" to keyDeserializer,
        "value.deserializer" to valueDeserializer,
        "group.id" to groupId,
        "auto.offset.reset" to autoOffsetReset,
        "enable.auto.commit" to enableAutoCommit

    )

}
