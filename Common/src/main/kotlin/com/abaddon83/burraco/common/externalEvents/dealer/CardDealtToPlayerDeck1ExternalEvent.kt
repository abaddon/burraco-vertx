package com.abaddon83.burraco.common.externalEvents.dealer

import com.abaddon83.burraco.common.models.DealerIdentity
import com.abaddon83.burraco.common.models.GameIdentity
import com.fasterxml.jackson.annotation.JsonProperty

data class CardDealtToPlayerDeck1ExternalEvent(
    @JsonProperty("aggregateIdentity")
    override val aggregateIdentity: DealerIdentity,
    @JsonProperty("gameIdentity")
    val gameIdentity: GameIdentity,
    @JsonProperty("cardLabel")
    val cardLabel: String,
) : DealerExternalEvent(aggregateIdentity, DealerEventName.CardDealtToPlayerDeck1)
