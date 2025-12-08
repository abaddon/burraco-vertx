package com.abaddon83.burraco.player.command.playerDraft

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.player.model.player.Player
import com.abaddon83.burraco.player.model.player.PlayerDraft
import com.abaddon83.burraco.player.model.player.PlayerWaiting
import io.github.abaddon.kcqrs.core.domain.messages.commands.Command

/**
 * Command to set a player to waiting state when the game starts and it's NOT their turn.
 *
 * Transitions: PlayerDraft → PlayerWaiting
 *
 * @param aggregateID The player to set waiting
 * @param gameIdentity The game they are playing in
 * @param teamMateId The player's teammate (optional, can be null)
 */
data class SetPlayerWaiting(
    override val aggregateID: PlayerIdentity,
    val gameIdentity: GameIdentity,
    val teamMateId: PlayerIdentity?
) : Command<Player>(aggregateID) {

    override fun execute(currentAggregate: Player?): Result<PlayerWaiting> = runCatching {
        when (currentAggregate) {
            is PlayerDraft -> currentAggregate.setWaiting(teamMateId)
            else -> throw UnsupportedOperationException(
                "Cannot set player waiting in state ${currentAggregate?.javaClass?.simpleName}. Player must be in PlayerDraft state."
            )
        }
    }
}
