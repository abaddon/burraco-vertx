package com.abaddon83.burraco.common.externalEvents.dealer

import com.abaddon83.burraco.common.models.DealerIdentity
import com.abaddon83.burraco.common.models.GameIdentity
import com.fasterxml.jackson.annotation.JsonProperty

data class DealingCompletedExternalEvent(
    @JsonProperty("aggregateIdentity")
    override val aggregateIdentity: DealerIdentity,
    @JsonProperty("gameIdentity")
    val gameIdentity: GameIdentity
) : DealerExternalEvent(aggregateIdentity, DealerEventName.DealingCompleted) {

    override fun extractEventKey(): String = gameIdentity.valueAsString()
}
