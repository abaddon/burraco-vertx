package com.abaddon83.burraco.common.externalEvents.dealer

import com.abaddon83.burraco.common.externalEvents.ExternalEvent
import com.abaddon83.burraco.common.models.GameIdentity
import io.github.abaddon.kcqrs.core.IIdentity

data class CardGivenToDeck(
    override val aggregateIdentity: IIdentity,
    val gameIdentity: GameIdentity,
    val cardLabel: String,
): ExternalEvent {
    override val eventOwner: String = "Dealer"
    override val eventName: String = this::class.java.simpleName
}
