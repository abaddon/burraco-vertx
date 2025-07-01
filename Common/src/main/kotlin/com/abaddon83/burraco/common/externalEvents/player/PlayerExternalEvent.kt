package com.abaddon83.burraco.common.externalEvents.player

import com.abaddon83.burraco.common.externalEvents.EventOwner
import com.abaddon83.burraco.common.externalEvents.ExternalEvent
import com.abaddon83.burraco.common.models.PlayerIdentity

abstract class PlayerExternalEvent(
    override val aggregateIdentity: PlayerIdentity,
    name: PlayerEventName
) : ExternalEvent {
    override val eventOwner: String = EventOwner.PLAYER.name
    override val eventName: String = name.name
}
