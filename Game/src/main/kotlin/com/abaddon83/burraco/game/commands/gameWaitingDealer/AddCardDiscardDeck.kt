package com.abaddon83.burraco.game.commands.gameWaitingDealer

import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.game.models.game.GameWaitingDealer
import io.github.abaddon.kcqrs.core.domain.messages.commands.Command

data class AddCardDiscardDeck(
    override val aggregateID: GameIdentity,
    val card: Card
) : Command<Game>(aggregateID) {

    override fun execute(currentAggregate: Game?): GameWaitingDealer = when (currentAggregate) {
        is GameWaitingDealer -> currentAggregate.addCardDiscardDeck(card)
        else -> throw UnsupportedOperationException("Aggregate in a wrong status")
    }
}
