package com.abaddon83.burraco.game.ports

import com.abaddon83.burraco.game.events.game.GameEvent


interface GameEventPublisherPort {

    suspend fun publish(event: GameEvent)

    fun onFailure(throwable: Throwable, event: GameEvent)

    fun onSuccess(event: GameEvent)

}
