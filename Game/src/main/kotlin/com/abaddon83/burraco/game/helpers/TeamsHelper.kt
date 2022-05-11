package com.abaddon83.burraco.game.helpers

import com.abaddon83.burraco.game.models.Team
import com.abaddon83.burraco.game.models.player.PlayerIdentity
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object TeamsHelper {
    val log: Logger = LoggerFactory.getLogger(this::class.simpleName)

    fun buildTeamsWith3Players(playerPickedUp: PlayerIdentity, players: List<PlayerIdentity>): List<Team> {
        require(players.contains(playerPickedUp)){"PlayerPickedUp ${playerPickedUp.valueAsString()} has to belong to the players ${players.map { it.valueAsString() }}"}
        require(players.size == 3){"Only 3 players are accepted"}
        return listOf(
            Team(listOf(playerPickedUp),true),
            Team(players.minus(playerPickedUp),false)
        )

    }


}