package com.abaddon83.burraco.game.adapters.dealerAdapter.config

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

data class KafkaDealerConsumerConfig(
    val properties: Map<String, String>
){
    fun consumerConfig(): Map<String, String> = properties

    fun toJson(): String{
        return Json.encodeToString(this);
    }

}
