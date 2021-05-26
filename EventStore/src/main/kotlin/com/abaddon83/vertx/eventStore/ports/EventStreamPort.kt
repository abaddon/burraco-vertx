package com.abaddon83.vertx.eventStore.ports

import com.abaddon83.vertx.eventStore.models.Event


interface EventStreamPort {

    fun publish(event: Event)
}