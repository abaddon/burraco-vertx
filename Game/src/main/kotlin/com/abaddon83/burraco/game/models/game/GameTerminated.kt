package com.abaddon83.burraco.game.models.game

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.game.models.Team
import com.abaddon83.burraco.game.models.player.ScorePlayer

data class GameTerminated constructor(
    override val id: GameIdentity,
    override val version: Long,
    override val players: List<ScorePlayer>,
    private val teams: List<Team>
) : Game() {

    companion object Factory {
        fun from(game: GameExecutionEndPhase): GameTerminated =
            GameTerminated(
                game.id,
                game.version,
                game.players.map { ScorePlayer.from(it) },
                game.teams
            )
    }

}
