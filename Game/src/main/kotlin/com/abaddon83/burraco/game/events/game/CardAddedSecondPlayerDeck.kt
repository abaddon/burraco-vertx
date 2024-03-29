package com.abaddon83.burraco.game.events.game

import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.common.models.GameIdentity
import io.github.abaddon.kcqrs.core.domain.messages.events.EventHeader
import java.util.*

data class CardAddedSecondPlayerDeck(
    override val messageId: UUID,
    override val header: EventHeader,
    override val aggregateId: GameIdentity,
    val card: Card
) : GameEvent() {
    companion object Factory {
        fun create(aggregateId: GameIdentity, card: Card): CardAddedSecondPlayerDeck =
            CardAddedSecondPlayerDeck(UUID.randomUUID(), EventHeader.create(AGGREGATE_TYPE_NAME), aggregateId, card)
    }

}
