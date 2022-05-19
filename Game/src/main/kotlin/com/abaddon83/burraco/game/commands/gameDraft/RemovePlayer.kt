package com.abaddon83.burraco.game.commands.gameDraft

import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameDraft
import com.abaddon83.burraco.game.models.game.GameIdentity
import com.abaddon83.burraco.game.models.player.PlayerIdentity
import io.github.abaddon.kcqrs.core.domain.messages.commands.Command

data class RemovePlayer(
    override val aggregateID: GameIdentity,
    val playerIdentity: PlayerIdentity
) : Command<Game>(aggregateID) {

    override fun execute(currentAggregate: Game?): GameDraft = when (currentAggregate) {
        is GameDraft -> currentAggregate.removePlayer(playerIdentity)
        else -> throw UnsupportedOperationException("Aggregate in a wrong status")
    }

}
