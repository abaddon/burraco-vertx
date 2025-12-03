package com.abaddon83.burraco.dealer.ports

import com.abaddon83.burraco.common.models.event.dealer.DealerEvent
import com.abaddon83.burraco.dealer.models.Dealer

interface ExternalEventPublisherPort {
    suspend fun publish(
        aggregate: Dealer,
        event: DealerEvent
    ): Result<Unit>

    /**
     * Phase 2 Optimization: Batch publish multiple events at once.
     * This reduces Kafka network calls from N to 1 for card dealing operations.
     */
    suspend fun publishBatch(
        aggregate: Dealer,
        events: List<DealerEvent>
    ): Result<Unit>
}