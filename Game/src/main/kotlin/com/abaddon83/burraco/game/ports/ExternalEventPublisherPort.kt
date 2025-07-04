package com.abaddon83.burraco.game.ports

import com.abaddon83.burraco.common.models.event.game.GameEvent
import com.abaddon83.burraco.game.models.game.Game


interface ExternalEventPublisherPort {
    suspend fun publish(aggregate: Game, event: GameEvent): Result<Unit>

}
