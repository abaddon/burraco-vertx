package com.abaddon83.burraco.game.commands.gameExecutionPlayPhase

import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameExecutionPlayPhase
import com.abaddon83.burraco.game.models.game.GameIdentity
import com.abaddon83.burraco.game.models.player.PlayerIdentity
import io.github.abaddon.kcqrs.core.domain.messages.commands.Command

data class DropCardOnDiscardPile(
    override val aggregateID: GameIdentity,
    val playerID: PlayerIdentity,
    val card: Card
) : Command<Game>(aggregateID) {

    override fun execute(currentAggregate: Game?): Game = when (currentAggregate) {
        is GameExecutionPlayPhase -> currentAggregate.dropCardOnDiscardPile(playerID, card)
        else -> throw UnsupportedOperationException("Aggregate in a wrong status")
    }
}
