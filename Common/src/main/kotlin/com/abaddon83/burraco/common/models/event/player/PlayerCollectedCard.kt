package com.abaddon83.burraco.common.models.event.player

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.card.Card
import io.github.abaddon.kcqrs.core.domain.messages.events.EventHeader
import java.util.*

data class PlayerCollectedCard(
    override val messageId: UUID,
    override val header: EventHeader,
    override val aggregateId: PlayerIdentity,
    val gameId: GameIdentity,
    val card: Card
) : PlayerEvent() {
    companion object Factory {
        fun create(aggregateId: PlayerIdentity, gameId: GameIdentity, card: Card): PlayerCollectedCard =
            PlayerCollectedCard(
                UUID.randomUUID(),
                EventHeader.create(AGGREGATE_TYPE_NAME),
                aggregateId,
                gameId,
                card
            )
    }
}