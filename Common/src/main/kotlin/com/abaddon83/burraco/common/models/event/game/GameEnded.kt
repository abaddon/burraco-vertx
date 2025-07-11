package com.abaddon83.burraco.common.models.event.game

import com.abaddon83.burraco.common.models.GameIdentity
import io.github.abaddon.kcqrs.core.domain.messages.events.EventHeader
import java.util.*

data class GameEnded(
    override val messageId: UUID,
    override val header: EventHeader,
    override val aggregateId: GameIdentity,
) : GameEvent() {
    companion object Factory {
        fun create(aggregateId: GameIdentity): GameEnded =
            GameEnded(UUID.randomUUID(), EventHeader.create(AGGREGATE_TYPE_NAME), aggregateId)
    }

}