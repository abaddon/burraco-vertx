package com.abaddon83.burraco.common.models.event.game

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.card.Card
import io.github.abaddon.kcqrs.core.domain.messages.events.EventHeader
import java.util.UUID

data class CardAddedPlayer(
    override val messageId: UUID,
    override val header: EventHeader,
    override val aggregateId: GameIdentity,
    val playerId: PlayerIdentity,
    val card: Card,
) : GameEvent() {
    companion object Factory {
        fun create(aggregateId: GameIdentity, playerId: PlayerIdentity, card: Card): CardAddedPlayer =
            CardAddedPlayer(UUID.randomUUID(), EventHeader.create(AGGREGATE_TYPE_NAME), aggregateId, playerId, card)
    }

}
