package com.abaddon83.vertx.burraco.game.ports

interface EventBrokerProducerPort<K,V> {

    fun publish(topic: String, key: K, value: V)
}