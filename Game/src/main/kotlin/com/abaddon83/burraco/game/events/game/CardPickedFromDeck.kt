package com.abaddon83.burraco.game.events.game

import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import io.github.abaddon.kcqrs.core.domain.messages.events.EventHeader
import java.util.*

data class CardPickedFromDeck(
    override val messageId: UUID,
    override val header: EventHeader,
    override val aggregateId: GameIdentity,
    val playerIdentity: PlayerIdentity,
    val card: Card
) : GameEvent() {
    companion object Factory {
        fun create(aggregateId: GameIdentity, playerIdentity: PlayerIdentity,card: Card): CardPickedFromDeck =
            CardPickedFromDeck(UUID.randomUUID(), EventHeader.create(AGGREGATE_TYPE_NAME), aggregateId, playerIdentity,card)
    }

}