package com.abaddon83.burraco.common.externalEvents.dealer

import com.abaddon83.burraco.common.models.DealerIdentity
import com.abaddon83.burraco.common.models.GameIdentity
import com.fasterxml.jackson.annotation.JsonProperty

data class CardDealtToDiscardDeckExternalEvent(
    @JsonProperty("aggregateIdentity")
    override val aggregateIdentity: DealerIdentity,
    @JsonProperty("gameIdentity")
    val gameIdentity: GameIdentity,
    @JsonProperty("cardLabel")
    val cardLabel: String,
) : DealerExternalEvent(aggregateIdentity, DealerEventName.CardDealtToDiscardDeck){

    override fun extractEventKey(): String = gameIdentity.valueAsString()
}
