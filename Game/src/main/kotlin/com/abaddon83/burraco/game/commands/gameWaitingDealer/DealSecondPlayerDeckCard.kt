package com.abaddon83.burraco.game.commands.gameWaitingDealer

import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameDraft
import com.abaddon83.burraco.game.models.game.GameIdentity
import com.abaddon83.burraco.game.models.game.GameWaitingDealer
import com.abaddon83.burraco.game.models.player.PlayerIdentity
import io.github.abaddon.kcqrs.core.domain.messages.commands.Command

data class DealSecondPlayerDeckCard(
    override val aggregateID: GameIdentity,
    val card: Card
) : Command<Game>(aggregateID) {

    override fun execute(currentAggregate: Game?): Game = when (currentAggregate) {
        is GameWaitingDealer -> currentAggregate.dealSecondPlayerDeckCard(card)
        else -> throw UnsupportedOperationException("Aggregate in a wrong status")
    }
}