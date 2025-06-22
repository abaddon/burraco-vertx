package com.abaddon83.burraco.dealer.commands

import com.abaddon83.burraco.dealer.events.DealerEvent
import com.abaddon83.burraco.dealer.models.Dealer
import com.abaddon83.burraco.dealer.ports.ExternalEventPublisherPort
import io.github.abaddon.kcqrs.core.domain.AggregateCommandHandler
import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log
import io.github.abaddon.kcqrs.core.persistence.IAggregateRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext

class AggregateDealerCommandHandler(
    repository: IAggregateRepository<Dealer>,
    private val externalEventPublisherPort: ExternalEventPublisherPort
) : AggregateCommandHandler<Dealer>(repository) {

    private val kafkaPublisherCoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override suspend fun onSuccess(updatedAggregate: Dealer): Result<Dealer> =
        withContext(kafkaPublisherCoroutineScope.coroutineContext) {
            runCatching {
                updatedAggregate
                    .uncommittedEvents()
                    .map {
                        when (it) {
                            is DealerEvent -> externalEventPublisherPort.publish(updatedAggregate, it)
                            else -> null
                        }
                    }
                updatedAggregate
            }.onFailure { ex ->
                log.error("Error while publishing events", ex)
            }
        }
}
