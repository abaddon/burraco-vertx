package com.abaddon83.burraco.dealer.events

import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.vertx.core.json.Json

abstract class DealerEvent : IDomainEvent {
    override val aggregateType: String = AGGREGATE_TYPE_NAME
    override val version: Long = 1

    fun toJson(): String= Json.encode(this);

    companion object {
        @JvmStatic
        protected val AGGREGATE_TYPE_NAME = "Dealer"
    }
}