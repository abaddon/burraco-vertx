package com.abaddon83.burraco.dealer.events

import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent

abstract class DealerEvent: IDomainEvent {
    override val aggregateType: String = AGGREGATE_TYPE_NAME
    override val version: Int = 1

    companion object {
        @JvmStatic
        protected val AGGREGATE_TYPE_NAME = "Dealer"
    }
}