package com.abaddon83.burraco.common.externalEvents.game

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.card.Card
import com.abaddon83.burraco.common.models.event.game.CardPickedFromDeck
import com.fasterxml.jackson.annotation.JsonProperty

data class CardPickedFromDeckExternalEvent(
    @JsonProperty("aggregateIdentity")
    override val aggregateIdentity: GameIdentity,
    @JsonProperty("playerIdentity")
    val playerIdentity: PlayerIdentity,
    @JsonProperty("card")
    val card: Card
) : GameExternalEvent(aggregateIdentity, GameEventName.CardPickedFromDeck) {

    fun toDomain(): CardPickedFromDeck = CardPickedFromDeck.create(aggregateIdentity, playerIdentity, card)

    companion object {
        fun fromDomain(domainEvent: CardPickedFromDeck): CardPickedFromDeckExternalEvent =
            CardPickedFromDeckExternalEvent(
                domainEvent.aggregateId,
                domainEvent.playerIdentity,
                domainEvent.card
            )
    }
}
