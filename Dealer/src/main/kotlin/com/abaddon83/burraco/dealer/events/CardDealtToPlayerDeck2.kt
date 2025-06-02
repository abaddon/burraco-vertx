package com.abaddon83.burraco.dealer.events

import com.abaddon83.burraco.dealer.models.Card
import com.abaddon83.burraco.common.models.DealerIdentity
import com.abaddon83.burraco.common.models.GameIdentity
import io.github.abaddon.kcqrs.core.domain.messages.events.EventHeader
import java.util.*

class CardDealtToPlayerDeck2 private constructor(
    override val messageId: UUID,
    override val header: EventHeader,
    override val aggregateId: DealerIdentity,
    val gameId: GameIdentity,
    val card: Card
) : DealerEvent() {
    companion object Factory {
        fun create(aggregateId: DealerIdentity, gameId: GameIdentity, card: Card): CardDealtToPlayerDeck2 =
            CardDealtToPlayerDeck2(UUID.randomUUID(), EventHeader.create(AGGREGATE_TYPE_NAME), aggregateId,gameId,card)
    }

}