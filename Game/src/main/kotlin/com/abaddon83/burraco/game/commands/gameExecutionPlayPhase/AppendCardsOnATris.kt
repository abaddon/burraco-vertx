package com.abaddon83.burraco.game.commands.gameExecutionPlayPhase

import com.abaddon83.burraco.common.models.TrisIdentity
import com.abaddon83.burraco.common.models.card.Card
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameExecutionPlayPhase
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import io.github.abaddon.kcqrs.core.domain.messages.commands.Command

data class AppendCardsOnATris(
    override val aggregateID: GameIdentity,
    val playerID: PlayerIdentity,
    val trisIdentity: TrisIdentity,
    val cards: List<Card>
) : Command<Game>(aggregateID) {

    override fun execute(currentAggregate: Game?): Result<GameExecutionPlayPhase> = runCatching{
        when (currentAggregate) {
            is GameExecutionPlayPhase -> currentAggregate.appendCardsOnTris(playerID, cards, trisIdentity)
            else -> throw UnsupportedOperationException("Aggregate in a wrong status")
        }
    }
}
