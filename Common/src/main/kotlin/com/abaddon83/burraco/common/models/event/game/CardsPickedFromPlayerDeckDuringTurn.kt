package com.abaddon83.burraco.common.models.event.game

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.card.Card
import io.github.abaddon.kcqrs.core.domain.messages.events.EventHeader
import java.util.UUID

data class CardsPickedFromPlayerDeckDuringTurn(
    override val messageId: UUID,
    override val header: EventHeader,
    override val aggregateId: GameIdentity,
    val playerIdentity: PlayerIdentity,
    val cards: List<Card>
) : GameEvent() {
    companion object Factory {
        fun create(aggregateId: GameIdentity, playerIdentity: PlayerIdentity,cards: List<Card>): CardsPickedFromPlayerDeckDuringTurn =
            CardsPickedFromPlayerDeckDuringTurn(UUID.randomUUID(), EventHeader.create(AGGREGATE_TYPE_NAME), aggregateId, playerIdentity,cards)
    }

}