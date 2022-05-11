package com.abaddon83.burraco.game.helpers

import com.abaddon83.burraco.game.models.Team
import com.abaddon83.burraco.game.models.player.PlayerIdentity

fun <TTeam : Team> Iterable<TTeam>.playerTeam(playerIdentity: PlayerIdentity): Team? =
    this.find { team -> team.members.contains(playerIdentity)  }

fun <TTeam : Team> Iterable<TTeam>.updateTeam(
    playerIdentity: PlayerIdentity,
    updateFunction: (team: TTeam) -> TTeam
): List<TTeam> = this
    .map { team ->
        when (team.members.contains(playerIdentity)) {
            true -> updateFunction(team)
            false -> team
        }
    }