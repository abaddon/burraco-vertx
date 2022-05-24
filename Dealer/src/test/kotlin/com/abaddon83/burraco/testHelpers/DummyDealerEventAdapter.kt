package com.abaddon83.burraco.testHelpers

import com.abaddon83.burraco.dealer.events.DealerEvent
import com.abaddon83.burraco.dealer.events.DeckCreated
import com.abaddon83.burraco.dealer.ports.EventsPort
import com.abaddon83.burraco.dealer.ports.EventsPortResult
import com.abaddon83.burraco.dealer.ports.PublishResult
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.security.InvalidParameterException

class DummyDealerEventAdapter : EventsPort {
    private val log: Logger = LoggerFactory.getLogger(this::class.simpleName)
    override suspend fun publish(event: IDomainEvent): EventsPortResult<Exception, PublishResult> =
        when (event) {
            is DealerEvent -> {
                log.debug("event published: $event")
                EventsPortResult.Valid(PublishResult(mapOf()))
            }
            else -> {
                log.error("event not recognised: $event")
                EventsPortResult.Invalid(Exception())
            }
        }


    override suspend fun publish(events: List<IDomainEvent>): List<EventsPortResult<Exception, PublishResult>> =
        events.map { publish(it)}
}
