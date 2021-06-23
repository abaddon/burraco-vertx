package com.abaddon83.eventStore.ports

import com.abaddon83.eventStore.models.Event
import com.abaddon83.utils.functionals.Validated
import com.abaddon83.eventStore.commands.EventError

typealias OutcomeDetail = Map<String,String>
typealias Outcome = Validated<EventError, OutcomeDetail>



interface RepositoryPort {
    fun save(event: Event): Validated<EventError, OutcomeDetail>

    fun findEvents(entityName: String, entityKey: String): List<Event>
}