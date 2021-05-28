package com.abaddon83.burraco.readModel.adapters.readModelRestAdapter.models

import com.abaddon83.burraco.readModel.projections.BurracoGame
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
                game.identity.id,
                "BURRACO",
                game.status.name,
                game.players.map { player -> player.id  }
            )
        }

//        private fun getStatus(game: BurracoGame): String{
//            return when(game){
//                is BurracoGameWaitingPlayers -> "DRAFT"
//                is BurracoGameExecutionTurnBeginning -> "STARTED"
//                is BurracoGameExecutionTurnExecution -> "RUNNING"
//                else -> "ENDED"
//            }
//        }
    }
}