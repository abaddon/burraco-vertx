package com.abaddon83.burraco.dealer.adapters.externalEventPublisher

import com.abaddon83.burraco.dealer.events.DealerEvent
import com.abaddon83.burraco.dealer.models.Dealer
import com.abaddon83.burraco.dealer.ports.ExternalEventPublisherPort
import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log

class DummyExternalEventPublisher : ExternalEventPublisherPort {
    override suspend fun publish(aggregate: Dealer, event: DealerEvent): Result<Unit> {
        log.info("${event::class.java.simpleName} published")
        return Result.success(Unit)
    }

}