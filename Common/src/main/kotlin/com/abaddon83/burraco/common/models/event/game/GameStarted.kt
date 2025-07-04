package com.abaddon83.burraco.common.models.event.game

import com.abaddon83.burraco.common.models.GameIdentity
import io.github.abaddon.kcqrs.core.domain.messages.events.EventHeader
import java.util.*

data class GameStarted(
    override val messageId: UUID,
    override val header: EventHeader,
    override val aggregateId: GameIdentity
) : GameEvent() {
    companion object Factory {
        fun create(aggregateId: GameIdentity): GameStarted =
            GameStarted(UUID.randomUUID(), EventHeader.create(AGGREGATE_TYPE_NAME), aggregateId)
    }

}