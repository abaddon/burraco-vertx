package com.abaddon83.burraco.testHelpers

import com.abaddon83.burraco.dealer.events.DealerEvent
import com.abaddon83.burraco.dealer.models.Dealer
import com.abaddon83.burraco.dealer.ports.ExternalEventPublisherPort
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DummyExternalEventPublisherAdapter : ExternalEventPublisherPort {
    private val log: Logger = LoggerFactory.getLogger(this::class.simpleName)
    override suspend fun publish(aggregate: Dealer, event: DealerEvent) {
        log.debug("event published: $event")
    }
}
