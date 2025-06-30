package com.abaddon83.burraco.player.command.playerDraft

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.player.model.player.PlayerDraft
import com.abaddon83.burraco.player.model.player.Player
import io.github.abaddon.kcqrs.core.domain.messages.commands.Command

data class CreatePlayer(
    override val aggregateID: PlayerIdentity,
    val gameIdentity: GameIdentity,
    val user: String
) : Command<Player>(aggregateID) {

    override fun execute(currentAggregate: Player?): Result<PlayerDraft> = runCatching {
        when (currentAggregate) {
            is PlayerDraft -> currentAggregate.createPlayer(aggregateID, user, gameIdentity)
            else -> throw UnsupportedOperationException("Aggregate in a wrong status")
        }
    }

}
