package com.abaddon83.vertx.eventStore.adapters.repositoryAdapter.inMemory

import com.abaddon83.utils.eventStore.model.Event
import io.vertx.core.logging.LoggerFactory

object InMemoryDB {
    private val log = LoggerFactory.getLogger(this::class.qualifiedName)
    private val repository: MutableList<Event> = mutableListOf()

    fun add(event: Event): Event? {
        return when (repository.add(event)) {
            true -> event
            false -> {
                log.error("event not saved")
                return null
            }
        }
    }
}