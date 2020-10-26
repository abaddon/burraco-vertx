package com.abaddon83.vertx.eventStore.ports

import com.abaddon83.utils.functionals.Validated
import com.abaddon83.vertx.eventStore.commands.EventError
import com.abaddon83.vertx.eventStore.models.Event

interface EventStreamPort {

    fun publish(event: Event): Validated<EventError, OutcomeDetail>
}