package com.abaddon83.burraco.common.adapter.kafka.consumer

import com.abaddon83.burraco.common.adapter.kafka.KafkaEvent
import io.vertx.core.Handler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

abstract class EventHandler(): Handler<KafkaEvent> {

    abstract suspend fun asyncHandle(event: KafkaEvent?)

    override fun handle(event: KafkaEvent?) {
        GlobalScope.launch() {
            asyncHandle(event)
        }

    }

}