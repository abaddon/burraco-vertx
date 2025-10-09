package com.abaddon83.burraco.common.externalEvents.game

import com.abaddon83.burraco.common.externalEvents.EventOwner
import com.abaddon83.burraco.common.externalEvents.ExternalEvent
import com.abaddon83.burraco.common.models.GameIdentity

abstract class GameExternalEvent(
    override val aggregateIdentity: GameIdentity,
    name: GameEventName
) : ExternalEvent {
    override val eventOwner: String = EventOwner.GAME.name
    override val eventName: String = name.name
}
