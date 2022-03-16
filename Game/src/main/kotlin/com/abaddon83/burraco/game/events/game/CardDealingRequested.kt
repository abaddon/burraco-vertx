package com.abaddon83.burraco.game.events.game

import com.abaddon83.burraco.game.models.game.GameIdentity
import com.abaddon83.burraco.game.models.player.PlayerIdentity
import io.github.abaddon.kcqrs.core.domain.messages.events.EventHeader
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import java.util.*

data class CardDealingRequested private constructor(
    override val messageId: UUID,
    override val header: EventHeader,
    override val aggregateId: GameIdentity,
    val requestedBy: PlayerIdentity
) : IDomainEvent {
    override val aggregateType: String = AGGREGATE_TYPE_NAME
    override val version: Int = 1

    companion object Factory {
        private const val AGGREGATE_TYPE_NAME = "Game"
        fun create(aggregateId: GameIdentity, requestedBy: PlayerIdentity): CardDealingRequested =
            CardDealingRequested(UUID.randomUUID(), EventHeader.create(AGGREGATE_TYPE_NAME), aggregateId, requestedBy)
    }

}