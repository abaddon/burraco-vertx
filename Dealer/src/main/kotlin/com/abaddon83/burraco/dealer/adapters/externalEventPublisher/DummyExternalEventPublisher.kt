package com.abaddon83.burraco.dealer.adapters.externalEventPublisher

import com.abaddon83.burraco.common.models.event.dealer.DealerEvent
import com.abaddon83.burraco.dealer.models.Dealer
import com.abaddon83.burraco.dealer.ports.ExternalEventPublisherPort
import io.github.abaddon.kcqrs.core.helpers.KcqrsLoggerFactory.log

class DummyExternalEventPublisher : ExternalEventPublisherPort {
    override suspend fun publish(aggregate: Dealer, event: DealerEvent): Result<Unit> {
        log.info("${event::class.java.simpleName} published")
        return Result.success(Unit)
    }

    override suspend fun publishBatch(aggregate: Dealer, events: List<DealerEvent>): Result<Unit> {
        log.info("Publishing batch of ${events.size} events")
        events.forEach { event ->
            log.info("  - ${event::class.java.simpleName}")
        }
        return Result.success(Unit)
    }

}