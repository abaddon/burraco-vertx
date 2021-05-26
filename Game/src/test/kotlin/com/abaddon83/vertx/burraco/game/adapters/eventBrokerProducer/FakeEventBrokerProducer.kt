package com.abaddon83.vertx.burraco.game.adapters.eventBrokerProducer

import com.abaddon83.utils.ddd.Event
import com.abaddon83.vertx.burraco.game.ports.EventBrokerProducerPort

class FakeEventBrokerProducer: EventBrokerProducerPort<String, String> {
    override fun publish(topic: String, event: Event) {

    }
}