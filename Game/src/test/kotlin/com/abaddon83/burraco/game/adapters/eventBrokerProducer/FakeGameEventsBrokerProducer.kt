package com.abaddon83.burraco.game.adapters.eventBrokerProducer

import com.abaddon83.utils.ddd.Event
import com.abaddon83.burraco.game.ports.GameEventsBrokerProducerPort

class FakeGameEventsBrokerProducer: GameEventsBrokerProducerPort<String, String> {
    override fun publish(topic: String, event: Event) {

    }
}