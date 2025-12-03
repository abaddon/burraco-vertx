package com.abaddon83.burraco.common.externalEvents.player

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.fasterxml.jackson.annotation.JsonProperty

data class PlayerCreated(
    @JsonProperty("aggregateIdentity")
    override val aggregateIdentity: PlayerIdentity,
    @JsonProperty("gameIdentity")
    val gameIdentity: GameIdentity
) : PlayerExternalEvent(aggregateIdentity, PlayerEventName.PlayerCreated) {

    override fun extractEventKey(): String = gameIdentity.valueAsString()

}
