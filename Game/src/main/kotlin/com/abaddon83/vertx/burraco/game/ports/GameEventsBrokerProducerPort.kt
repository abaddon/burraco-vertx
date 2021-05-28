package com.abaddon83.vertx.burraco.game.ports

import com.abaddon83.utils.ddd.Event

interface GameEventsBrokerProducerPort<K,V> {

    fun publish(topic: String, event: Event)
}