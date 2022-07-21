package com.abaddon83.burraco.game.helpers

import com.abaddon83.burraco.game.models.Team
import com.abaddon83.burraco.common.models.PlayerIdentity

fun Iterable<Team>.playerTeam(playerIdentity: PlayerIdentity): Team? =
    this.find { team -> team.members.contains(playerIdentity)  }

fun Iterable<Team>.updateTeam(
    playerIdentity: PlayerIdentity,
    updateFunction: (team: Team) -> Team
): List<Team> = this
    .map { team ->
        when (team.members.contains(playerIdentity)) {
            true -> updateFunction(team)
            false -> team
        }
    }