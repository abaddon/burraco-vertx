package com.abaddon83.vertx.burraco.engine.adapters.eventStoreVertx


import com.abaddon83.vertx.burraco.engine.adapters.eventStoreVertx.model.ExtendEvent
import io.vertx.core.AsyncResult
import io.vertx.core.Handler

interface EventStoreService {

    fun persist(event: ExtendEvent, resultHandler: Handler<AsyncResult<Boolean>>);
}
