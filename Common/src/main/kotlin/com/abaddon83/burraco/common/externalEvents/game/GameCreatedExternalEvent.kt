package com.abaddon83.burraco.common.externalEvents.game

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.event.game.GameCreated
import com.fasterxml.jackson.annotation.JsonProperty

data class GameCreatedExternalEvent(
    @JsonProperty("aggregateIdentity")
    override val aggregateIdentity: GameIdentity
) : GameExternalEvent(aggregateIdentity, GameEventName.GameCreated) {

    fun toDomain(): GameCreated = GameCreated.create(aggregateIdentity)

    companion object {
        fun fromDomain(domainEvent: GameCreated): GameCreatedExternalEvent =
            GameCreatedExternalEvent(domainEvent.aggregateId)

    }
}

