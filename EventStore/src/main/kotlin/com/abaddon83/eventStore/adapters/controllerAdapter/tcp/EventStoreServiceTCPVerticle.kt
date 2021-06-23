package com.abaddon83.eventStore.adapters.controllerAdapter.tcp

import com.abaddon83.eventStore.adapters.controllerAdapter.tcp.config.EventStoreServiceTCPConfig
import com.abaddon83.eventStore.adapters.controllerAdapter.tcp.handlers.EventBusPublishHandler
import com.abaddon83.eventStore.adapters.controllerAdapter.tcp.handlers.EventBusQueryHandler
import com.abaddon83.eventStore.ports.ControllerPort
import io.vertx.core.AsyncResult
import io.vertx.core.eventbus.MessageConsumer
import io.vertx.core.json.JsonObject
import io.vertx.ext.bridge.BridgeOptions
import io.vertx.ext.bridge.PermittedOptions
import io.vertx.ext.eventbus.bridge.tcp.TcpEventBusBridge
import io.vertx.kotlin.coroutines.CoroutineVerticle
import org.slf4j.LoggerFactory


class EventStoreServiceTCPVerticle(private val controller: ControllerPort, val eventStoreConfig: EventStoreServiceTCPConfig) : CoroutineVerticle() {

    private val log = LoggerFactory.getLogger(this::class.java.simpleName)
    private var consumer: MessageConsumer<JsonObject>? = null


    override suspend fun start() {
        vertx.eventBus().consumer(eventStoreConfig.channels.publish, EventBusPublishHandler(controller = controller))
        vertx.eventBus().consumer(eventStoreConfig.channels.query, EventBusQueryHandler(controller = controller))

        val bridge = TcpEventBusBridge.create(
            vertx,
            BridgeOptions()
                .addInboundPermitted(PermittedOptions())
                .addOutboundPermitted(PermittedOptions())
        )

        bridge.listen(eventStoreConfig.service.port,eventStoreConfig.service.address) { res: AsyncResult<TcpEventBusBridge?> ->
            if (res.failed()) {
                    log.error("Bridge failed",res.cause())
            }
        }
    }

//    override fun persist(event: Event): Outcome {
//        val cmd= PersistEventCmd(event)
//        return when (val cmdResult = commandHandle.handle(cmd)){
//            is Invalid -> cmdResult
//            is Valid -> {
//                commandHandle.handle(PublishEventCmd(event))
//                cmdResult
//            }
//        }
//    }
//
//    override fun getEntityEvents(entityName: String, entityKey: String): List<Event> {
//        val query = GetEntityEvents(entityName = entityName, identity = entityKey)
//        val queryResult = queryHandler.handle(query)
//        return queryResult.response
//    }
}