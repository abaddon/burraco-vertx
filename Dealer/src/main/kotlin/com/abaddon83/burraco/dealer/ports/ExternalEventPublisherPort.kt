package com.abaddon83.burraco.dealer.ports

import com.abaddon83.burraco.dealer.events.DealerEvent
import com.abaddon83.burraco.dealer.models.Dealer

interface ExternalEventPublisherPort {
    suspend fun publish(
        aggregate: Dealer,
        event: DealerEvent
    ): Result<Unit>
}