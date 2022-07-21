package com.abaddon83.burraco.game.commands.gameDraft

import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameDraft
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import io.github.abaddon.kcqrs.core.domain.messages.commands.Command

data class CreateGame(
    override val aggregateID: GameIdentity
) : Command<Game>(aggregateID) {

    override fun execute(currentAggregate: Game?): GameDraft = when (currentAggregate) {
        is GameDraft -> currentAggregate.createGame(aggregateID)
        else -> throw UnsupportedOperationException("Aggregate in a wrong status")
    }

}
