package com.abaddon83.burraco.common.externalEvents.game

import com.abaddon83.burraco.common.models.GameIdentity
import com.fasterxml.jackson.annotation.JsonProperty

data class GameCreatedExternalEvent(
    @JsonProperty("aggregateIdentity")
    override val aggregateIdentity: GameIdentity
) : GameExternalEvent(aggregateIdentity, GameEventName.CardsRequestedToDealer)
