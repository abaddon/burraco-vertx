package com.abaddon83.vertx.eventStore.queries

import com.abaddon83.vertx.eventStore.models.Event
import com.abaddon83.vertx.eventStore.ports.RepositoryPort
import io.vertx.core.logging.LoggerFactory


data class QueryResult(val query: Query, val response: List<Event>)



class QueryHandler(readModelRepository: RepositoryPort){
    companion object{
        private val log = LoggerFactory.getLogger(this::class.qualifiedName)
    }

    private val repository = readModelRepository

    fun handle(q: Query):QueryResult {

        return  QueryResult(q, processQuery(q))

    }

    private fun processQuery(q: Query): List<Event> {
        return when(q){
            is GetEntityEvents -> repository.findEvents(q.entityName,q.identity)
        }
    }



}

