package com.abaddon83.burraco.common.externalEvents.dealer

import com.abaddon83.burraco.common.externalEvents.ExternalEvent
import com.abaddon83.burraco.common.models.DealerIdentity
import com.abaddon83.burraco.common.models.GameIdentity
import com.fasterxml.jackson.annotation.JsonProperty
import io.github.abaddon.kcqrs.core.IIdentity

data class CardGivenToDeck(
    @JsonProperty("aggregateIdentity")
    override val aggregateIdentity: DealerIdentity,
    @JsonProperty("gameIdentity")
    val gameIdentity: GameIdentity,
    @JsonProperty("cardLabel")
    val cardLabel: String,
): ExternalEvent {
    override val eventOwner: String = "Dealer"
    override val eventName: String = this::class.java.simpleName
}
