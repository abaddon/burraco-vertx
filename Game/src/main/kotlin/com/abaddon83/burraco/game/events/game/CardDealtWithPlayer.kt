package com.abaddon83.burraco.game.events.game

import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.game.GameIdentity
import com.abaddon83.burraco.game.models.player.PlayerIdentity
import io.github.abaddon.kcqrs.core.domain.messages.events.EventHeader
import java.util.*

data class CardDealtWithPlayer(
    override val messageId: UUID,
    override val header: EventHeader,
    override val aggregateId: GameIdentity,
    val playerId: PlayerIdentity,
    val card: Card,
) : GameEvent() {
    companion object Factory {
        fun create(aggregateId: GameIdentity, playerId: PlayerIdentity, card: Card): CardDealtWithPlayer =
            CardDealtWithPlayer(UUID.randomUUID(), EventHeader.create(AGGREGATE_TYPE_NAME), aggregateId, playerId, card)
    }

}
