package com.abaddon83.burraco.common.models.event.dealer

import com.abaddon83.burraco.common.models.DealerIdentity
import com.abaddon83.burraco.common.models.GameIdentity
import io.github.abaddon.kcqrs.core.domain.messages.events.EventHeader
import java.util.UUID

data class DealingCompleted(
    override val messageId: UUID,
    override val header: EventHeader,
    override val aggregateId: DealerIdentity,
    val gameId: GameIdentity
) : DealerEvent() {
    companion object Factory {
        fun create(aggregateId: DealerIdentity, gameId: GameIdentity): DealingCompleted =
            DealingCompleted(UUID.randomUUID(), EventHeader.create(AGGREGATE_TYPE_NAME), aggregateId, gameId)
    }
}
