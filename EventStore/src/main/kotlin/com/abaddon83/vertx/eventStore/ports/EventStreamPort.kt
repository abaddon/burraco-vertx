package com.abaddon83.vertx.eventStore.ports

import com.abaddon83.utils.eventStore.model.Event


interface EventStreamPort {

    fun publish(event: Event)
}