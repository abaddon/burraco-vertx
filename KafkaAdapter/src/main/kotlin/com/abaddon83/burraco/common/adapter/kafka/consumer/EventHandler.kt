package com.abaddon83.burraco.common.adapter.kafka.consumer

import com.abaddon83.burraco.common.adapter.kafka.KafkaEvent
import io.vertx.core.Handler

abstract class EventHandler : Handler<KafkaEvent> {


}