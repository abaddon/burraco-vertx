package com.abaddon83.burraco.dealer.commands

import com.abaddon83.burraco.common.commandController.AggregateCommandHandler
import com.abaddon83.burraco.dealer.events.DealerEvent
import com.abaddon83.burraco.dealer.models.Dealer
import com.abaddon83.burraco.dealer.ports.ExternalEventPublisherPort
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.github.abaddon.kcqrs.core.persistence.IAggregateRepository

class AggregateDealerCommandHandler(
    override val repository: IAggregateRepository<Dealer>,
    private val externalEventPublisherPort: ExternalEventPublisherPort
) : AggregateCommandHandler<Dealer>() {

    protected override suspend fun onSuccess(aggregate: Dealer) {
        aggregate
            .uncommittedEvents()
            .map {
                when (it) {
                    is DealerEvent -> externalEventPublisherPort.publish(aggregate, it)
                    else -> null
                }
            }
//        val externalEventPublisherErrors =
//            externalEventPublisherResults.filterIsInstance<ExternalEventPublisherResult.Invalid<Exception>>()
//        when (externalEventPublisherErrors.isEmpty()) {
//            true -> log.debug("Events ${eventsToString(aggregate.uncommittedEvents())} published")
//            false -> log.error("Events ${eventsToString(aggregate.uncommittedEvents())} couldn't published", externalEventPublisherErrors)
//        }
    }

    private fun eventsToString(events: List<IDomainEvent>): String = events.joinToString(separator = ", ") { it.messageId.toString() }
}