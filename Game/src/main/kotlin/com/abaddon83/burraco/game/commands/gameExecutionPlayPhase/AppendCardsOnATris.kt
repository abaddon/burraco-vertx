package com.abaddon83.burraco.game.commands.gameExecutionPlayPhase

import com.abaddon83.burraco.game.models.TrisIdentity
import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameExecutionPlayPhase
import com.abaddon83.burraco.game.models.game.GameIdentity
import com.abaddon83.burraco.game.models.player.PlayerIdentity
import io.github.abaddon.kcqrs.core.domain.messages.commands.Command

data class AppendCardsOnATris(
    override val aggregateID: GameIdentity,
    val playerID: PlayerIdentity,
    val trisIdentity: TrisIdentity,
    val cards: List<Card>
) : Command<Game>(aggregateID) {

    override fun execute(currentAggregate: Game?): Game = when (currentAggregate) {
        is GameExecutionPlayPhase -> currentAggregate.appendCardsOnATris(playerID, cards, trisIdentity)
        else -> throw UnsupportedOperationException("Aggregate in a wrong status")
    }
}
