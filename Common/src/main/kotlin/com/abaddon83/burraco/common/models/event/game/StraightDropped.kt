package com.abaddon83.burraco.common.models.event.game

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.StraightIdentity
import com.abaddon83.burraco.common.models.card.Card
import io.github.abaddon.kcqrs.core.domain.messages.events.EventHeader
import java.util.UUID

data class StraightDropped(
    override val messageId: UUID,
    override val header: EventHeader,
    override val aggregateId: GameIdentity,
    val playerIdentity: PlayerIdentity,
    val straightIdentity: StraightIdentity,
    val cards: List<Card>
) : GameEvent() {
    companion object Factory {
        fun create(
            aggregateId: GameIdentity,
            playerIdentity: PlayerIdentity,
            straightIdentity: StraightIdentity,
            straightCards: List<Card>
        ): StraightDropped =
            StraightDropped(
                UUID.randomUUID(),
                EventHeader.create(AGGREGATE_TYPE_NAME),
                aggregateId,
                playerIdentity,
                straightIdentity,
                straightCards
            )
    }

}