package com.abaddon83.burraco.game.events.game

import com.abaddon83.burraco.game.models.game.GameIdentity
import com.abaddon83.burraco.game.models.player.PlayerIdentity
import io.github.abaddon.kcqrs.core.domain.messages.events.EventHeader
import java.util.*

data class GameStarted private constructor(
    override val messageId: UUID,
    override val header: EventHeader,
    override val aggregateId: GameIdentity
) : GameEvent() {
    companion object Factory {
        fun create(aggregateId: GameIdentity): GameStarted =
            GameStarted(UUID.randomUUID(), EventHeader.create(AGGREGATE_TYPE_NAME), aggregateId)
    }

}