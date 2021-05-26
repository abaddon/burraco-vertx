package com.abaddon83.vertx.eventStore.adapters.repositoryAdapter.inMemory


import com.abaddon83.utils.functionals.Invalid
import com.abaddon83.utils.functionals.Valid
import com.abaddon83.utils.functionals.Validated
import com.abaddon83.vertx.eventStore.commands.EventError
import com.abaddon83.vertx.eventStore.models.Event
import com.abaddon83.vertx.eventStore.ports.OutcomeDetail
import com.abaddon83.vertx.eventStore.ports.RepositoryPort

class InMemoryRepositoryAdapter: RepositoryPort {

    override fun save(event: Event): Validated<EventError, OutcomeDetail> {
        return when(InMemoryDB.add(event)){
            is Event -> Valid(mapOf("event" to event.jsonPayload))
            else -> Invalid(EventError("Event not saved"))
        }
    }

    override fun findEvents(entityName: String, entityKey: String): List<Event> {
        TODO("Not yet implemented")
    }
}