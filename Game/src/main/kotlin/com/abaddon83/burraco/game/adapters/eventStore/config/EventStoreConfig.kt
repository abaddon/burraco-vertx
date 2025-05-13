package com.abaddon83.burraco.game.adapters.eventStore.config

import io.github.abaddon.kcqrs.eventstoredb.config.EventStoreDBConfig
import io.github.abaddon.kcqrs.eventstoredb.eventstore.EventStoreDBRepositoryConfig
import kotlinx.serialization.Serializable

@Serializable
data class EventStoreConfig(
    val streamName: String,
    val maxReadPageSize: Long,
    val maxWritePageSize: Int,
    val eventStoreDB: EventStoreDB
) {

    fun eventStoreDBRepositoryConfig(): EventStoreDBRepositoryConfig =
        EventStoreDBRepositoryConfig(
            EventStoreDBConfig(
                eventStoreDB.connectionString
            ),
            streamName,
            maxReadPageSize,
            maxWritePageSize
        )

    @Serializable
    data class EventStoreDB(
        val connectionString: String
    )
}
