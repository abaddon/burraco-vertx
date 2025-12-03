package com.abaddon83.burraco.dealer.commands

import com.abaddon83.burraco.common.models.event.dealer.DealerEvent
import com.abaddon83.burraco.dealer.models.Dealer
import com.abaddon83.burraco.dealer.ports.ExternalEventPublisherPort
import io.github.abaddon.kcqrs.core.domain.AggregateCommandHandler
import io.github.abaddon.kcqrs.core.helpers.KcqrsLoggerFactory.log
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

    /**
     * Phase 2 Optimization: Batch publish all events at once.
     * Collects all uncommitted DealerEvents and publishes them in a single batch,
     * reducing Kafka network calls from N to 1.
     */
    override suspend fun onSuccess(updatedAggregate: Dealer): Result<Dealer> =
        withContext(kafkaPublisherCoroutineScope.coroutineContext) {
            runCatching {
                val dealerEvents = updatedAggregate
                    .uncommittedEvents()
                    .filterIsInstance<DealerEvent>()

                if (dealerEvents.isNotEmpty()) {
                    log.info("Publishing batch of ${dealerEvents.size} dealer events")
                    externalEventPublisherPort.publishBatch(updatedAggregate, dealerEvents)
                        .onFailure { ex ->
                            log.error("Failed to publish batch of ${dealerEvents.size} events", ex)
                            throw ex
                        }
                    log.info("Successfully published batch of ${dealerEvents.size} dealer events")
                }

                updatedAggregate
            }.onFailure { ex ->
                log.error("Error while publishing events", ex)
            }
        }
}
