package com.abaddon83.burraco.game.commands.gameExecutionEndPhase

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameExecutionEndPhase
import com.abaddon83.burraco.game.models.game.GameExecutionPickUpPhase
import io.github.abaddon.kcqrs.core.domain.messages.commands.Command

data class StartNextPlayerTurn(
    override val aggregateID: GameIdentity,
) : Command<Game>(aggregateID) {

    override fun execute(currentAggregate: Game?): Result<GameExecutionPickUpPhase> = runCatching {
        when (currentAggregate) {
            is GameExecutionEndPhase -> currentAggregate.startNextPlayerTurn()
            else -> throw UnsupportedOperationException("Aggregate in a wrong status")
        }
    }

}
