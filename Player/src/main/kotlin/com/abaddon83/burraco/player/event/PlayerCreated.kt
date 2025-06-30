package com.abaddon83.burraco.player.event

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import io.github.abaddon.kcqrs.core.domain.messages.events.EventHeader
import java.util.UUID

data class PlayerCreated(
    override val messageId: UUID,
    override val header: EventHeader,
    override val aggregateId: PlayerIdentity,
    val gameIdentity: GameIdentity,
    val user: String,

    ) : PlayerEvent() {
    companion object Factory {
        fun create(aggregateId: PlayerIdentity, gameIdentity: GameIdentity, user: String): PlayerCreated =
            PlayerCreated(
                UUID.randomUUID(),
                EventHeader.create(AGGREGATE_TYPE_NAME),
                aggregateId,
                gameIdentity,
                user
            )
    }
}