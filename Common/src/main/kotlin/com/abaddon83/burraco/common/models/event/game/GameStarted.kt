package com.abaddon83.burraco.common.models.event.game

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import io.github.abaddon.kcqrs.core.domain.messages.events.EventHeader
import java.util.*

data class GameStarted(
    override val messageId: UUID,
    override val header: EventHeader,
    override val aggregateId: GameIdentity,
    val playerTurn: PlayerIdentity,
    val teams: List<List<PlayerIdentity>>
) : GameEvent() {
    companion object Factory {
        fun create(
            aggregateId: GameIdentity,
            playerTurn: PlayerIdentity,
            teams: List<List<PlayerIdentity>>
        ): GameStarted =
            GameStarted(
                UUID.randomUUID(),
                EventHeader.create(AGGREGATE_TYPE_NAME),
                aggregateId,
                playerTurn,
                teams
            )
    }

}