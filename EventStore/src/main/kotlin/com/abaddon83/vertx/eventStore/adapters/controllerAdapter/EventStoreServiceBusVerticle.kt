package com.abaddon83.vertx.eventStore.adapters.controllerAdapter

import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.vertx.core.eventbus.MessageConsumer
import io.vertx.core.json.JsonObject
import io.vertx.core.json.jackson.DatabindCodec
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.serviceproxy.ServiceBinder

class EventStoreServiceBusVerticle : CoroutineVerticle() {
    companion object {
        const val SERVICE_ADDRESS = "eventstore-bus-service-address"
    }

    private val service by lazy {
        EventStoreServiceImpl(vertx)
    }

    private var consumer: MessageConsumer<JsonObject>? = null

    override suspend fun start() {
        DatabindCodec.mapper().registerModule(KotlinModule())


        consumer = ServiceBinder(vertx)
            .setAddress(SERVICE_ADDRESS)
            .register(EventStoreService::class.java, service)
    }

    override suspend fun stop() {
        if (consumer != null)
            ServiceBinder(vertx).unregister(consumer)
    }

}