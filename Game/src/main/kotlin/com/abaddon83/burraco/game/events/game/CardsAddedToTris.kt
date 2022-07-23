package com.abaddon83.burraco.game.events.game

import com.abaddon83.burraco.common.models.TrisIdentity
import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import io.github.abaddon.kcqrs.core.domain.messages.events.EventHeader
import java.util.*

data class CardsAddedToTris private constructor(
    override val messageId: UUID,
    override val header: EventHeader,
    override val aggregateId: GameIdentity,
    val playerIdentity: PlayerIdentity,
    val trisIdentity: TrisIdentity,
    val cards: List<Card>
) : GameEvent() {
    companion object Factory {
        fun create(aggregateId: GameIdentity, playerIdentity: PlayerIdentity, trisIdentity: TrisIdentity, cards: List<Card>): CardsAddedToTris =
            CardsAddedToTris(UUID.randomUUID(), EventHeader.create(AGGREGATE_TYPE_NAME), aggregateId, playerIdentity,trisIdentity, cards)
    }

}