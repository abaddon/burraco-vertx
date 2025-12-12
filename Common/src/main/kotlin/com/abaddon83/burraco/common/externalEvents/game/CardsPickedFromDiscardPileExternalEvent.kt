package com.abaddon83.burraco.common.externalEvents.game

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.card.Card
import com.abaddon83.burraco.common.models.event.game.CardsPickedFromDiscardPile
import com.fasterxml.jackson.annotation.JsonProperty

data class CardsPickedFromDiscardPileExternalEvent(
    @JsonProperty("aggregateIdentity")
    override val aggregateIdentity: GameIdentity,
    @JsonProperty("playerIdentity")
    val playerIdentity: PlayerIdentity,
    @JsonProperty("cards")
    val cards: List<Card>
) : GameExternalEvent(aggregateIdentity, GameEventName.CardsPickedFromDiscardPile) {

    fun toDomain(): CardsPickedFromDiscardPile = CardsPickedFromDiscardPile.create(aggregateIdentity, playerIdentity, cards)

    companion object {
        fun fromDomain(domainEvent: CardsPickedFromDiscardPile): CardsPickedFromDiscardPileExternalEvent =
            CardsPickedFromDiscardPileExternalEvent(
                domainEvent.aggregateId,
                domainEvent.playerIdentity,
                domainEvent.cards
            )
    }
}
