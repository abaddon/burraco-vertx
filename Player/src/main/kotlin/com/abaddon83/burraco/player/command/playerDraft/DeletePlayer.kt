package com.abaddon83.burraco.player.command.playerDraft

import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.player.model.player.PlayerNotInGame
import com.abaddon83.burraco.player.model.player.PlayerDraft
import com.abaddon83.burraco.player.model.player.Player
import io.github.abaddon.kcqrs.core.domain.messages.commands.Command

data class DeletePlayer(
    override val aggregateID: PlayerIdentity
) : Command<Player>(aggregateID) {

    override fun execute(currentAggregate: Player?): Result<PlayerNotInGame> = runCatching {
        when (currentAggregate) {
            is PlayerDraft -> currentAggregate.deletePlayer()
            else -> throw UnsupportedOperationException("Aggregate in a wrong status")
        }
    }
}