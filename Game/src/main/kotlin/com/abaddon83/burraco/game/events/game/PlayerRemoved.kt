package com.abaddon83.burraco.game.events.game

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import io.github.abaddon.kcqrs.core.domain.messages.events.EventHeader
import java.util.*

data class PlayerRemoved(
    override val messageId: UUID,
    override val header: EventHeader,
    override val aggregateId: GameIdentity,
    val playerIdentity: PlayerIdentity
) : GameEvent() {
    companion object Factory {
        fun create(aggregateId: GameIdentity, playerIdentity: PlayerIdentity): PlayerRemoved =
            PlayerRemoved(UUID.randomUUID(), EventHeader.create(AGGREGATE_TYPE_NAME), aggregateId, playerIdentity)
    }

}
