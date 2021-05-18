package com.abaddon83.vertx.burraco.game.adapters.eventBrokerConsumerAdapter.kafka.config

data class KafkaProducerConfig(
    val bootstrapServer: String,
    val keySerializer: String,
    val valueSerializer: String,
    val acks: String

) {

    fun producerConfig(): Map<String, String> = mapOf(
        "bootstrap.servers" to bootstrapServer,
        "key.serializer" to keySerializer,
        "value.serializer" to valueSerializer,
        "acks" to acks,
    )

}
