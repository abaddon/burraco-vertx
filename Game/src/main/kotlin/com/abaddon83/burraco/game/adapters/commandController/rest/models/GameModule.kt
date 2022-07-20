package com.abaddon83.burraco.game.adapters.commandController.rest.models
import com.abaddon83.burraco.game.models.game.*

data class GameModule(
    val gameId: String,
    val status: String,
    val players: List<String>
): Module() {
    companion object {
        fun from(game: Game): GameModule {
            return GameModule(
                game.id.valueAsString(),
                getStatus(game),
                game.players.map { player -> player.id.valueAsString()  }
            )
        }

        private fun getStatus(game: Game): String{
            return when(game){
                is GameDraft -> "DRAFT"
                is GameWaitingDealer -> "STARTED"
                is GameExecution -> "RUNNING"
                is GameTerminated -> "ENDED"
                else -> "UNKNOWN"
            }
        }
    }
}