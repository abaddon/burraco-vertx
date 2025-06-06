package com.abaddon83.burraco.game.commands.gameExecutionPickUpPhase

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameExecutionPickUpPhase
import com.abaddon83.burraco.game.models.game.GameExecutionPlayPhase
import io.github.abaddon.kcqrs.core.domain.messages.commands.Command

data class PickUpACardFromDeck(
    override val aggregateID: GameIdentity,
    val playerID: PlayerIdentity
) : Command<Game>(aggregateID) {

    override fun execute(currentAggregate: Game?): Result<GameExecutionPlayPhase> = runCatching {
        when (currentAggregate) {
            is GameExecutionPickUpPhase -> currentAggregate.pickUpACardFromDeck(playerID)
            else -> throw UnsupportedOperationException("Aggregate in a wrong status")
        }
    }
}
