package com.abaddon83.burraco.game.commands.gameWaitingDealer

import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.game.models.game.GameWaitingDealer
import com.abaddon83.burraco.common.models.PlayerIdentity
import io.github.abaddon.kcqrs.core.domain.messages.commands.Command

data class AddCardPlayer(
    override val aggregateID: GameIdentity,
    val playerIdentity: PlayerIdentity,
    val card: Card
) : Command<Game>(aggregateID) {

    override fun execute(currentAggregate: Game?): GameWaitingDealer = when (currentAggregate) {
        is GameWaitingDealer -> currentAggregate.addCardPlayer(playerIdentity,card)
        else -> throw UnsupportedOperationException("Aggregate in a wrong status")
    }
}
