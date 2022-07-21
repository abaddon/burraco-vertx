package com.abaddon83.burraco.common.externalEvents.game

import com.abaddon83.burraco.common.externalEvents.ExternalEvent
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.fasterxml.jackson.annotation.JsonProperty

data class CardsRequestedToDealer(
    @JsonProperty("aggregateIdentity")
    override val aggregateIdentity: GameIdentity,
    @JsonProperty("players")
    val players: List<PlayerIdentity>
): ExternalEvent{
    override val eventOwner: String = "Game"
    override val eventName: String = this::class.java.simpleName
}
