package com.abaddon83.burraco.game.commands

import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameIdentity
import com.abaddon83.burraco.game.models.game.GameWaitingDealer
import io.github.abaddon.kcqrs.core.domain.messages.commands.Command

data class StartGame(
    override val aggregateID: GameIdentity
) : Command<Game>(aggregateID) {

    override fun execute(currentAggregate: Game?): Game = when (currentAggregate) {
        is GameWaitingDealer -> currentAggregate.startGame()
        else -> throw UnsupportedOperationException("Aggregate in a wrong status")
    }

}
