package com.abaddon83.burraco.dealer.events

import com.abaddon83.burraco.dealer.models.Card
import com.abaddon83.burraco.common.models.DealerIdentity
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import io.github.abaddon.kcqrs.core.domain.messages.events.EventHeader
import java.util.*

data class DeckCreated private constructor(
    override val messageId: UUID,
    override val header: EventHeader,
    override val aggregateId: DealerIdentity,
    val gameId: GameIdentity,
    val playersId: List<PlayerIdentity>,
    val cards: List<Card>
) : DealerEvent() {
    companion object Factory {
        fun create(aggregateId: DealerIdentity,gameId: GameIdentity,playersId: List<PlayerIdentity>,cards: List<Card>): DeckCreated =
            DeckCreated(UUID.randomUUID(), EventHeader.create(AGGREGATE_TYPE_NAME), aggregateId,gameId,playersId,cards)
    }

}