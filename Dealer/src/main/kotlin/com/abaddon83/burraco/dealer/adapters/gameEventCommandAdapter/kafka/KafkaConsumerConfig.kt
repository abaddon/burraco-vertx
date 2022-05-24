package com.abaddon83.burraco.dealer.adapters.gameEventCommandAdapter.kafka

import org.apache.kafka.clients.consumer.ConsumerConfig

data class KafkaConsumerConfig(
    val topic: String,
    val bootstrapServer: String,
    val groupId: String = "dealer",
    val keyDeserializer: String = "org.apache.kafka.common.serialization.StringDeserializer",
    val valueDeserializer: String = "org.apache.kafka.common.serialization.StringDeserializer",
    val autoOffsetReset: String = "earliest",
    val enableAutoCommit: Boolean = false,
    val pollingDurationMs:  Long = 100L


) {

    fun consumerConfig(): Map<String, String> = mapOf(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServer,
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to keyDeserializer,
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to valueDeserializer,
        ConsumerConfig.GROUP_ID_CONFIG to groupId,
        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to autoOffsetReset,
        ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to enableAutoCommit.toString()

    )

}
