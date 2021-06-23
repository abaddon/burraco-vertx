package com.abaddon83.eventStore.adapters.controllerAdapter.tcp


import com.abaddon83.utils.functionals.Invalid
import com.abaddon83.utils.functionals.Valid
import com.abaddon83.eventStore.commands.PersistEventCmd
import com.abaddon83.eventStore.models.Event
import com.abaddon83.eventStore.ports.ControllerPort
import com.abaddon83.eventStore.ports.Outcome
import com.abaddon83.eventStore.ports.RepositoryPort
import com.abaddon83.eventStore.queries.GetEntityEvents

class ControllerAdapter(override val repository: RepositoryPort) : ControllerPort {

    override fun persist(event: Event): Outcome {
        val cmd= PersistEventCmd(event)
        return when (val cmdResult = commandHandle.handle(cmd)){
            is Invalid -> cmdResult
            is Valid -> {
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