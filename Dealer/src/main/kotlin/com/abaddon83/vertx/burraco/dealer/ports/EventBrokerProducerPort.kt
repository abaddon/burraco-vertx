package com.abaddon83.vertx.burraco.dealer.ports

import com.abaddon83.utils.ddd.Event

interface EventBrokerProducerPort<K,V> {

    fun publish(topic: String, event: Event)
}