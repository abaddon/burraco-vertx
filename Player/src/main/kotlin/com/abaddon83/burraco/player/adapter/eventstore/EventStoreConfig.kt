package com.abaddon83.burraco.player.adapter.eventstore

import io.github.abaddon.kcqrs.eventstoredb.config.EventStoreDBConfig
import io.github.abaddon.kcqrs.eventstoredb.eventstore.EventStoreDBRepositoryConfig
import kotlinx.serialization.Serializable

@Serializable
data class EventStoreConfig(
    val streamName: String,
    val maxReadPageSize: Long,
    val maxWritePageSize: Int,
    val connectionString: String
) {

    fun eventStoreDBRepositoryConfig(): EventStoreDBRepositoryConfig =
        EventStoreDBRepositoryConfig(
            EventStoreDBConfig(
                connectionString
            ),
            streamName,
            maxReadPageSize,
            maxWritePageSize
        )

    companion object {
        fun empty(): EventStoreConfig = EventStoreConfig("", 1, 1, "")
    }
}
