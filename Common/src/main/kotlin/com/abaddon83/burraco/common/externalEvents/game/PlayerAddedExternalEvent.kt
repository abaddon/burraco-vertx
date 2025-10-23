package com.abaddon83.burraco.common.externalEvents.game

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.event.game.PlayerAdded
import com.fasterxml.jackson.annotation.JsonProperty

data class PlayerAddedExternalEvent(
    @JsonProperty("aggregateIdentity")
    override val aggregateIdentity: GameIdentity,
    @JsonProperty("playerIdentity")
    val playerIdentity: PlayerIdentity
) : GameExternalEvent(aggregateIdentity, GameEventName.PlayerAdded) {

    fun toDomain(): PlayerAdded = PlayerAdded.create(aggregateIdentity, playerIdentity)

    companion object {
        fun fromDomain(domainEvent: PlayerAdded): PlayerAddedExternalEvent =
            PlayerAddedExternalEvent(domainEvent.aggregateId, domainEvent.playerIdentity)

    }
}

