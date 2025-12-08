package com.abaddon83.burraco.player.command.playerDraft

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.player.model.player.Player
import com.abaddon83.burraco.player.model.player.PlayerActive
import com.abaddon83.burraco.player.model.player.PlayerDraft
import io.github.abaddon.kcqrs.core.domain.messages.commands.Command

/**
 * Command to activate a player when the game starts and it's their turn.
 *
 * Transitions: PlayerDraft → PlayerActive
 *
 * @param aggregateID The player to activate
 * @param gameIdentity The game they are playing in
 * @param teamMateId The player's teammate (optional, can be null)
 */
data class ActivatePlayer(
    override val aggregateID: PlayerIdentity,
    val gameIdentity: GameIdentity,
    val teamMateId: PlayerIdentity?
) : Command<Player>(aggregateID) {

    override fun execute(currentAggregate: Player?): Result<PlayerActive> = runCatching {
        when (currentAggregate) {
            is PlayerDraft -> currentAggregate.activatePlayer(teamMateId)
            else -> throw UnsupportedOperationException(
                "Cannot activate player in state ${currentAggregate?.javaClass?.simpleName}. Player must be in PlayerDraft state."
            )
        }
    }
}
