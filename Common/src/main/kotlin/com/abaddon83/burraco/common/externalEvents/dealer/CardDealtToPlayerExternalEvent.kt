package com.abaddon83.burraco.common.externalEvents.dealer

import com.abaddon83.burraco.common.models.DealerIdentity
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.fasterxml.jackson.annotation.JsonProperty

data class CardDealtToPlayerExternalEvent(
    @JsonProperty("aggregateIdentity")
    override val aggregateIdentity: DealerIdentity,
    @JsonProperty("gameIdentity")
    val gameIdentity: GameIdentity,
    @JsonProperty("playerIdentity")
    val playerIdentity: PlayerIdentity,
    @JsonProperty("cardLabel")
    val cardLabel: String,
) : DealerExternalEvent(aggregateIdentity, DealerEventName.CardDealtToPlayer){

    override fun extractEventKey(): String = gameIdentity.valueAsString()
}