package com.abaddon83.burraco.game.commands.gameExecutionPlayPhase

import com.abaddon83.burraco.game.models.Tris
import com.abaddon83.burraco.common.models.TrisIdentity
import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameExecutionPlayPhase
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import io.github.abaddon.kcqrs.core.domain.messages.commands.Command

data class DropTris(
                               override val aggregateID: GameIdentity,
                               val playerID: PlayerIdentity,
                               val trisID: TrisIdentity,
                               val cards: List<Card>
) : Command<Game>(aggregateID) {

    override fun execute(currentAggregate: Game?): GameExecutionPlayPhase = when (currentAggregate) {
        is GameExecutionPlayPhase -> currentAggregate.dropTris(playerID, Tris.create(trisID,cards))
        else -> throw UnsupportedOperationException("Aggregate in a wrong status")
    }
}
