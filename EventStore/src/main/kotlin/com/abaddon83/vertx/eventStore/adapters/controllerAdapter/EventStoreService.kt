package com.abaddon83.vertx.eventStore.adapters.controllerAdapter

import com.abaddon83.vertx.eventStore.adapters.controllerAdapter.model.ExtendEvent
import io.vertx.codegen.annotations.ProxyGen
import io.vertx.codegen.annotations.VertxGen
import io.vertx.core.AsyncResult
import io.vertx.core.Handler

@VertxGen
@ProxyGen
interface EventStoreService {

    fun persist(event: ExtendEvent, resultHandler: Handler<AsyncResult<Boolean>>);
}

//object EventStoreServiceFactory{
//    fun create(): EventStoreService = EventStoreServiceImpl()
//
//    fun createProxy(vertx: Vertx, address: String, options: DeliveryOptions = DeliveryOptions()): EventStoreService =
//        EventStoreServiceVertxEBProxy(vertx, address, options)
//}
