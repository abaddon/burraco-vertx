package com.abaddon83.burraco.game.commands.gameExecutionEndPhase

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameExecutionEndPhase
import com.abaddon83.burraco.game.models.game.GameTerminated
import io.github.abaddon.kcqrs.core.domain.messages.commands.Command

data class EndGame(
    override val aggregateID: GameIdentity,
) : Command<Game>(aggregateID) {

    override fun execute(currentAggregate: Game?): Result<GameTerminated> = runCatching {
        when (currentAggregate) {
            is GameExecutionEndPhase -> currentAggregate.endGame()
            else -> throw UnsupportedOperationException("Aggregate in a wrong status")
        }
    }
}
