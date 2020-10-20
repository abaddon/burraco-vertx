package com.abaddon83.vertx.eventStore.ports

import com.abaddon83.vertx.eventStore.commands.CommandHandler
import com.abaddon83.vertx.eventStore.models.Event
import com.abaddon83.vertx.eventStore.queries.QueryHandler


interface ControllerPort {

    val repository: RepositoryPort

    val commandHandle: CommandHandler
        get() = CommandHandler(repository)

    val queryHandler: QueryHandler
        get() = QueryHandler(repository)

    fun persist(event:Event): Outcome

    fun getEntityEvents(entityName: String, entityKey: String ): Set<Event>
}