package com.abaddon83.burraco.game.adapters.commandController.models
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.burracoGameExecutions.BurracoGameExecutionTurnBeginning
import com.abaddon83.burraco.game.models.burracoGameExecutions.BurracoGameExecutionTurnExecution
import java.util.*

data class GameModule(
    val gameId: UUID,
    val type: String,
    val status: String,
    val players: List<UUID>
): Module() {
    companion object {
        fun from(game: Game): GameModule {
            return GameModule(
                game.identity().id,
                "BURRACO",
                getStatus(game),
                game.players.map { player -> player.identity().id  }
            )
        }

        private fun getStatus(game: Game): String{
            return when(game){
                is BurracoGameWaitingPlayers -> "DRAFT"
                is BurracoGameExecutionTurnBeginning -> "STARTED"
                is BurracoGameExecutionTurnExecution -> "RUNNING"
                else -> "ENDED"
            }
        }
    }
}