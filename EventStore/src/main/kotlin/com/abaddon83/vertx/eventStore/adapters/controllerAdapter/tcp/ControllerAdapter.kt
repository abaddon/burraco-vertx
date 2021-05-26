package com.abaddon83.vertx.eventStore.adapters.controllerAdapter.tcp


import com.abaddon83.utils.functionals.Invalid
import com.abaddon83.utils.functionals.Valid
import com.abaddon83.vertx.eventStore.commands.PersistEventCmd
import com.abaddon83.vertx.eventStore.commands.PublishEventCmd
import com.abaddon83.vertx.eventStore.models.Event
import com.abaddon83.vertx.eventStore.ports.ControllerPort
import com.abaddon83.vertx.eventStore.ports.EventStreamPort
import com.abaddon83.vertx.eventStore.ports.Outcome
import com.abaddon83.vertx.eventStore.ports.RepositoryPort
import com.abaddon83.vertx.eventStore.queries.GetEntityEvents

class ControllerAdapter(override val repository: RepositoryPort, override val eventStream: EventStreamPort) : ControllerPort {

    override fun persist(event: Event): Outcome {
        val cmd= PersistEventCmd(event)
        return when (val cmdResult = commandHandle.handle(cmd)){
            is Invalid -> cmdResult
            is Valid -> {
                commandHandle.handle(PublishEventCmd(event))
                cmdResult
            }
        }
    }

    override fun getEntityEvents(entityName: String, entityKey: String): List<Event> {
        val query = GetEntityEvents(entityName = entityName, identity = entityKey)
        val queryResult = queryHandler.handle(query)
        return queryResult.response
    }
}