package com.abaddon83.burraco.common.models.event.player

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import io.github.abaddon.kcqrs.core.domain.messages.events.EventHeader
import java.util.UUID

data class PlayerWaitingTurn(
    override val messageId: UUID,
    override val header: EventHeader,
    override val aggregateId: PlayerIdentity,
    val gameIdentity: GameIdentity,
    val teamMateId: PlayerIdentity?
) : PlayerEvent() {
    companion object Factory {
        fun create(aggregateId: PlayerIdentity, gameIdentity: GameIdentity, teamMateId: PlayerIdentity?): PlayerWaitingTurn =
            PlayerWaitingTurn(
                UUID.randomUUID(),
                EventHeader.create(AGGREGATE_TYPE_NAME),
                aggregateId,
                gameIdentity,
                teamMateId
            )
    }
}
