package com.abaddon83.eventStore.adapters.repositoryAdapter.inMemory


import com.abaddon83.eventStore.models.Event
import org.slf4j.LoggerFactory


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