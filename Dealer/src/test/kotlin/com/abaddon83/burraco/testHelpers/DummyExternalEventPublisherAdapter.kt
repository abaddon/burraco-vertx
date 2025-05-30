package com.abaddon83.burraco.testHelpers


import com.abaddon83.burraco.dealer.events.DealerEvent
import com.abaddon83.burraco.dealer.models.Dealer
import com.abaddon83.burraco.dealer.ports.ExternalEventPublisherPort
import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log

class DummyExternalEventPublisherAdapter : ExternalEventPublisherPort {
    override suspend fun publish(aggregate: Dealer, event: DealerEvent): Result<Unit> {
        log.debug("event published: $event")
        return Result.success(Unit)
    }
}
