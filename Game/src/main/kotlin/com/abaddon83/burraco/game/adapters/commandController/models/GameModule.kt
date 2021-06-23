package com.abaddon83.burraco.game.adapters.commandController.models
import com.abaddon83.burraco.game.models.BurracoGame
import com.abaddon83.burraco.game.models.burracoGameExecutions.BurracoGameExecutionTurnBeginning
import com.abaddon83.burraco.game.models.burracoGameExecutions.BurracoGameExecutionTurnExecution
import com.abaddon83.burraco.game.models.burracoGameWaitingPlayers.BurracoGameWaitingPlayers
import java.util.*

data class GameModule(
    val gameId: UUID,
    val type: String,
    val status: String,
    val players: List<UUID>
): Module() {
    companion object {
        fun from(game: BurracoGame): GameModule {
            return GameModule(
                game.identity().id,
                "BURRACO",
                getStatus(game),
                game.players.map { player -> player.identity().id  }
            )
        }

        private fun getStatus(game: BurracoGame): String{
            return when(game){
                is BurracoGameWaitingPlayers -> "DRAFT"
                is BurracoGameExecutionTurnBeginning -> "STARTED"
                is BurracoGameExecutionTurnExecution -> "RUNNING"
                else -> "ENDED"
            }
        }
    }
}