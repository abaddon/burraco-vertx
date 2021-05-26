package com.abaddon83.vertx.eventStore.adapters.eventStreamAdapter


import com.abaddon83.vertx.eventStore.models.Event
import com.abaddon83.vertx.eventStore.ports.EventStreamPort


class FakeEventStreamAdapter : EventStreamPort {
    override fun publish(event: Event) {

    }
}