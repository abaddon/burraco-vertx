package com.abaddon83.burraco.player.adapter.projection

import io.github.abaddon.kcqrs.eventstoredb.config.EventStoreDBConfig
import io.github.abaddon.kcqrs.eventstoredb.eventstore.EventStoreSubscriptionConfig
import kotlinx.serialization.Serializable

@Serializable
data class PlayerViewProjectionConfig(
    val streamNames: List<String>,
    val groupName: String,
    val connectionString: String
) {
    fun eventStoreSubscriptionConfig(): EventStoreSubscriptionConfig =
        EventStoreSubscriptionConfig(
            EventStoreDBConfig(connectionString),
            streamNames,
            groupName
        )

    companion object {
        fun empty(): PlayerViewProjectionConfig = PlayerViewProjectionConfig(listOf(), "", "")
    }
}