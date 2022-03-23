package com.abaddon83.burraco.game.events.game

import com.abaddon83.burraco.game.models.Straight
import com.abaddon83.burraco.game.models.StraightIdentity
import com.abaddon83.burraco.game.models.TrisIdentity
import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.game.GameIdentity
import com.abaddon83.burraco.game.models.player.PlayerIdentity
import io.github.abaddon.kcqrs.core.domain.messages.events.EventHeader
import java.util.*

data class StraightDropped private constructor(
    override val messageId: UUID,
    override val header: EventHeader,
    override val aggregateId: GameIdentity,
    val playerIdentity: PlayerIdentity,
    val straightIdentity: StraightIdentity,
    val cards: List<Card>
) : GameEvent() {
    companion object Factory {
        fun create(aggregateId: GameIdentity, playerIdentity: PlayerIdentity, straight: Straight): StraightDropped =
            StraightDropped(UUID.randomUUID(), EventHeader.create(AGGREGATE_TYPE_NAME), aggregateId, playerIdentity, straight.id, straight.cards)
    }

}