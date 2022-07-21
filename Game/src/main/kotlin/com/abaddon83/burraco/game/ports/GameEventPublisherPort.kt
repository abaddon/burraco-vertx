package com.abaddon83.burraco.game.ports

import com.abaddon83.burraco.game.events.game.GameEvent
import com.abaddon83.burraco.game.models.game.Game


interface GameEventPublisherPort {
    suspend fun publish(aggregate: Game, event: GameEvent)

}
