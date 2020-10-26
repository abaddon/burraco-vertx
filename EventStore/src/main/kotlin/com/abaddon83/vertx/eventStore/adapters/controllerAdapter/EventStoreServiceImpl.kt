package com.abaddon83.vertx.eventStore.adapters.controllerAdapter

import com.abaddon83.utils.functionals.Invalid
import com.abaddon83.utils.functionals.Valid
import com.abaddon83.vertx.eventStore.adapters.controllerAdapter.model.ExtendEvent
import com.abaddon83.vertx.eventStore.adapters.eventStreamAdapter.KafkaEventStreamAdapter
import com.abaddon83.vertx.eventStore.adapters.repositoryAdapter.mysql.MysqlRepositoryAdapter
import com.abaddon83.vertx.eventStore.commands.CmdResult
import com.abaddon83.vertx.eventStore.commands.PersistEventCmd
import com.abaddon83.vertx.eventStore.models.Event
import com.abaddon83.vertx.eventStore.ports.ControllerPort
import com.abaddon83.vertx.eventStore.ports.EventStreamPort
import com.abaddon83.vertx.eventStore.ports.Outcome
import com.abaddon83.vertx.eventStore.ports.RepositoryPort
import com.abaddon83.vertx.eventStore.queries.GetEntityEvents
import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.Vertx


class EventStoreServiceImpl(vertx: Vertx) : EventStoreService, ControllerPort {

    private val vertx: Vertx = vertx
    override val repository: RepositoryPort
        //get() = VertxMysqlRepositoryAdapter(vertx) //Vertx Mysql Repository Adapter
        //get() = InMemoryRepositoryAdapter() //In Memory repository Adapter
        get() = MysqlRepositoryAdapter()

    override val eventStream: EventStreamPort
        get() = KafkaEventStreamAdapter(vertx)

    override fun persist(event: ExtendEvent, resultHandler: Handler<AsyncResult<Boolean>>) {
        val result = when (persist(event)) {
            is Valid -> true
            is Invalid -> false
        }
        resultHandler.handle(Future.succeededFuture(result))
    }

    override fun getEntityEvents(
        entityName: String,
        entityKey: String,
        resultHandler: Handler<AsyncResult<Set<ExtendEvent>>>
    ) {
        //val events = setOf<ExtendEvent>()
        val events = getEntityEvents(entityName,entityKey).map { ev ->
            ExtendEvent(ev)
        }.toSet()
        return resultHandler.handle(Future.succeededFuture(events))
    }

    override fun persist(event: Event): Outcome {
        val cmd= PersistEventCmd(event)
        val cmdResult = commandHandle.handle(cmd)
        return CmdResultAdapter.toOutcome(cmdResult = cmdResult);
    }

    override fun getEntityEvents(entityName: String, entityKey: String): Set<Event> {
        val query = GetEntityEvents(entityName = entityName, identity = entityKey)
        val queryResult = queryHandler.handle(query)
        return queryResult.response
    }
}

object CmdResultAdapter {
    fun toOutcome(cmdResult: CmdResult): Outcome {
        return when (cmdResult) {
            is Valid -> Valid(cmdResult.value)
            is Invalid -> Invalid(cmdResult.err)
        }
    }

}