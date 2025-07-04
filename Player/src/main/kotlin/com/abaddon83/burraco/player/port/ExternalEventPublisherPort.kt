package com.abaddon83.burraco.player.port

import com.abaddon83.burraco.common.models.event.player.PlayerEvent
import com.abaddon83.burraco.player.model.player.Player

interface ExternalEventPublisherPort {
    suspend fun publish(aggregate: Player, event: PlayerEvent): Result<Unit>
}