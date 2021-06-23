package com.abaddon83.burraco.game.adapters.dealerAdapter.config

data class KafkaDealerConsumerConfig(
    val properties: Map<String, String>
){
    fun consumerConfig(): Map<String, String> = properties

}
