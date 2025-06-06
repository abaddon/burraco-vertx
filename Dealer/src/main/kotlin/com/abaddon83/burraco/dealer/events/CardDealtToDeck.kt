package com.abaddon83.burraco.dealer.events

import com.abaddon83.burraco.dealer.models.Card
import com.abaddon83.burraco.common.models.DealerIdentity
import com.abaddon83.burraco.common.models.GameIdentity
import io.github.abaddon.kcqrs.core.domain.messages.events.EventHeader
import java.util.*

class CardDealtToDeck private constructor(
    override val messageId: UUID,
    override val header: EventHeader,
    override val aggregateId: DealerIdentity,
    val gameId: GameIdentity,
    val card: Card
) : DealerEvent() {
    companion object Factory {
        fun create(aggregateId: DealerIdentity, gameId: GameIdentity, card: Card): CardDealtToDeck =
            CardDealtToDeck(UUID.randomUUID(), EventHeader.create(AGGREGATE_TYPE_NAME), aggregateId,gameId,card)
    }

}