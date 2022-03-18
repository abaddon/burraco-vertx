package com.abaddon83.burraco.game.commands.gameExecutionPickUpPhase

import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameExecutionPickUpPhase
import com.abaddon83.burraco.game.models.game.GameIdentity
import com.abaddon83.burraco.game.models.player.PlayerIdentity
import io.github.abaddon.kcqrs.core.domain.messages.commands.Command

data class PickUpCardsFromDiscardPile(
                               override val aggregateID: GameIdentity,
                               val playerID: PlayerIdentity
) : Command<Game>(aggregateID) {

    override fun execute(currentAggregate: Game?): Game = when (currentAggregate) {
        is GameExecutionPickUpPhase -> currentAggregate.pickUpCardsFromDiscardPile(playerID)
        else -> throw UnsupportedOperationException("Aggregate in a wrong status")
    }
}
