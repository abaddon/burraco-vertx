package com.abaddon83.burraco.common.adapter.kafka.consumer

import com.abaddon83.burraco.common.adapter.kafka.KafkaEvent
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

abstract class EventHandler(val vertx: Vertx): Handler<KafkaEvent> {

    abstract suspend fun asyncHandle(event: KafkaEvent?)

    @OptIn(DelicateCoroutinesApi::class)
    override fun handle(event: KafkaEvent?) {
        GlobalScope.launch(vertx.dispatcher()) {
            asyncHandle(event)
        }

    }

}