package com.abaddon83.burraco.common.models.event.dealer

import com.abaddon83.burraco.common.models.DealerIdentity
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.card.Card
import io.github.abaddon.kcqrs.core.domain.messages.events.EventHeader
import java.util.UUID

data class CardDealtToPlayer(
    override val messageId: UUID,
    override val header: EventHeader,
    override val aggregateId: DealerIdentity,
    val gameId: GameIdentity,
    val playerId: PlayerIdentity,
    val card: Card
) : DealerEvent() {
    companion object Factory {
        fun create(aggregateId: DealerIdentity, gameId: GameIdentity, playerId: PlayerIdentity, card: Card): CardDealtToPlayer =
            CardDealtToPlayer(UUID.randomUUID(), EventHeader.create(AGGREGATE_TYPE_NAME), aggregateId,gameId,playerId,card)
    }

}