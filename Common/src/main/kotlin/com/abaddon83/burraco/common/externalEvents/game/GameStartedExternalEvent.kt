package com.abaddon83.burraco.common.externalEvents.game

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.event.game.GameStarted
import com.fasterxml.jackson.annotation.JsonProperty

data class GameStartedExternalEvent(
    @JsonProperty("aggregateIdentity")
    override val aggregateIdentity: GameIdentity,
    @JsonProperty("playerTurn")
    val playerTurn: PlayerIdentity,
    @JsonProperty("teams")
    val teams: List<List<PlayerIdentity>>
) : GameExternalEvent(aggregateIdentity, GameEventName.GameStarted) {

    fun toDomain(): GameStarted = GameStarted.create(aggregateIdentity, playerTurn, teams)

    companion object {
        fun fromDomain(domainEvent: GameStarted): GameStartedExternalEvent =
            GameStartedExternalEvent(
                domainEvent.aggregateId,
                domainEvent.playerTurn,
                domainEvent.teams
            )
    }
}
