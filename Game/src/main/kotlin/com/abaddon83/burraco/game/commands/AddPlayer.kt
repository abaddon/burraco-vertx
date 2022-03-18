package com.abaddon83.burraco.game.commands

import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameDraft
import com.abaddon83.burraco.game.models.game.GameIdentity
import com.abaddon83.burraco.game.models.player.PlayerIdentity
import io.github.abaddon.kcqrs.core.domain.messages.commands.Command

data class AddPlayer(
    override val aggregateID: GameIdentity,
    val playerIdentity: PlayerIdentity
) : Command<Game>(aggregateID) {

    override fun execute(currentAggregate: Game?): Game = when (currentAggregate) {
        is GameDraft -> currentAggregate.addPlayer(playerIdentity)
        else -> throw UnsupportedOperationException("Aggregate in a wrong status")
    }

}
