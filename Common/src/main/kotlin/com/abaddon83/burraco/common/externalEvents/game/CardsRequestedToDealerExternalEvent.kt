package com.abaddon83.burraco.common.externalEvents.game

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.fasterxml.jackson.annotation.JsonProperty

data class CardsRequestedToDealerExternalEvent(
    @JsonProperty("aggregateIdentity")
    override val aggregateIdentity: GameIdentity,
    @JsonProperty("players")
    val players: List<PlayerIdentity>
) : GameExternalEvent(aggregateIdentity, GameEventName.CardsRequestedToDealer) {

    companion object {
//        fun fromDomain(domainEvent: CardDealingRequested): CardsRequestedToDealerExternalEvent {
//            val listPlayerIdentities = (aggregate as GameWaitingDealer).players.map { it.id }
//            CardsRequestedToDealerExternalEvent(domainEvent.aggregateId, listPlayerIdentities)
//        }


    }
}
