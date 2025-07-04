package com.abaddon83.burraco.game.commands.gameExecutionPlayPhase

import com.abaddon83.burraco.game.models.Straight
import com.abaddon83.burraco.common.models.StraightIdentity
import com.abaddon83.burraco.common.models.card.Card
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameExecutionPlayPhase
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import io.github.abaddon.kcqrs.core.domain.messages.commands.Command

data class DropStraight(
    override val aggregateID: GameIdentity,
    val playerID: PlayerIdentity,
    val straightID: StraightIdentity,
    val cards: List<Card>
) : Command<Game>(aggregateID) {

    override fun execute(currentAggregate: Game?): Result<GameExecutionPlayPhase> = runCatching{
        when (currentAggregate) {
            is GameExecutionPlayPhase -> currentAggregate.dropStraight(playerID, Straight.create(straightID,cards))
            else -> throw UnsupportedOperationException("Aggregate in a wrong status")
        }
    }
}
