package com.abaddon83.burraco.common.adapter.kafka.consumer

import com.abaddon83.burraco.common.adapter.kafka.KafkaEvent
import io.vertx.core.Handler
import io.vertx.core.Vertx
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

abstract class EventHandler(protected val vertx: Vertx) : Handler<KafkaEvent> {


}