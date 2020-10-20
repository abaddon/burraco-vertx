package com.abaddon83.vertx.burraco.engine.adapters.eventStoreAdapter.vertx


import com.abaddon83.vertx.burraco.engine.adapters.eventStoreAdapter.vertx.model.ExtendEvent
import io.vertx.core.AsyncResult
import io.vertx.core.Handler

interface EventStoreService {

    fun persist(event: ExtendEvent, resultHandler: Handler<AsyncResult<Boolean>>);

    fun getEntityEvents(entityName: String, entityKey: String, resultHandler: Handler<AsyncResult<Set<ExtendEvent>>>);
}
