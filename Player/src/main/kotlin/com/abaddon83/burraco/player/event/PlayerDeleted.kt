package com.abaddon83.burraco.player.event

import com.abaddon83.burraco.common.models.PlayerIdentity
import io.github.abaddon.kcqrs.core.domain.messages.events.EventHeader
import java.util.UUID

data class PlayerDeleted(
    override val messageId: UUID,
    override val header: EventHeader,
    override val aggregateId: PlayerIdentity
) : PlayerEvent() {
    companion object Factory {
        fun create(aggregateId: PlayerIdentity): PlayerDeleted =
            PlayerDeleted(
                UUID.randomUUID(),
                EventHeader.create(AGGREGATE_TYPE_NAME),
                aggregateId
            )
    }
}