package com.abaddon83.burraco.game.commands.gameWaitingDealer

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameExecutionPickUpPhase
import com.abaddon83.burraco.game.models.game.GameWaitingDealer
import io.github.abaddon.kcqrs.core.domain.messages.commands.Command

data class StartPlayerTurn(
    override val aggregateID: GameIdentity
) : Command<Game>(aggregateID) {

    override fun execute(currentAggregate: Game?): Result<GameExecutionPickUpPhase> = runCatching {
        when (currentAggregate) {
            is GameWaitingDealer -> currentAggregate.startGame()
            else -> throw UnsupportedOperationException("Aggregate in a wrong status")
        }
    }

}
