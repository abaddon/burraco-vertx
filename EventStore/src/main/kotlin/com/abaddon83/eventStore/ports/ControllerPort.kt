package com.abaddon83.eventStore.ports

import com.abaddon83.eventStore.commands.CommandHandler
import com.abaddon83.eventStore.models.Event
import com.abaddon83.eventStore.queries.QueryHandler


interface ControllerPort {

    val repository: RepositoryPort

    val commandHandle: CommandHandler
        get() = CommandHandler(repository)

    val queryHandler: QueryHandler
        get() = QueryHandler(repository)

    fun persist(event: Event): Outcome

    fun getEntityEvents(entityName: String, entityKey: String ): List<Event>
}