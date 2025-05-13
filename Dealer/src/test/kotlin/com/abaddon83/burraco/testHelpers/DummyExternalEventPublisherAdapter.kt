package com.abaddon83.burraco.testHelpers

import com.abaddon83.burraco.common.helpers.log
import com.abaddon83.burraco.dealer.events.DealerEvent
import com.abaddon83.burraco.dealer.models.Dealer
import com.abaddon83.burraco.dealer.ports.ExternalEventPublisherPort

class DummyExternalEventPublisherAdapter : ExternalEventPublisherPort {
    override suspend fun publish(aggregate: Dealer, event: DealerEvent) {
        log.debug("event published: $event")
    }
}
