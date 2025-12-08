package com.abaddon83.burraco.player.adapter.commandController

import com.abaddon83.burraco.common.helpers.Validated
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.card.Card
import com.abaddon83.burraco.player.DomainError
import com.abaddon83.burraco.player.DomainResult
import com.abaddon83.burraco.player.command.playerDraft.ActivatePlayer
import com.abaddon83.burraco.player.command.playerDraft.AddCardToPlayer
import com.abaddon83.burraco.player.command.playerDraft.CreatePlayer
import com.abaddon83.burraco.player.command.playerDraft.DeletePlayer
import com.abaddon83.burraco.player.command.playerDraft.SetPlayerWaiting
import com.abaddon83.burraco.player.model.player.Player
import com.abaddon83.burraco.player.port.CommandControllerPort
import com.abaddon83.burraco.player.port.Outcome
import io.github.abaddon.kcqrs.core.domain.IAggregateCommandHandler
import io.github.abaddon.kcqrs.core.domain.messages.commands.ICommand

class CommandControllerAdapter(override val playerCommandHandler: IAggregateCommandHandler<Player>) :
    CommandControllerPort {

    override suspend fun createPlayer(gameIdentity: GameIdentity, user: String): Outcome {
        val cmd = CreatePlayer(PlayerIdentity.create(), gameIdentity, user)
        return executeCommand(cmd)
    }

    override suspend fun deletePlayer(playerIdentity: PlayerIdentity): Outcome {
        val cmd = DeletePlayer(playerIdentity)
        return executeCommand(cmd)
    }

    override suspend fun addCardToPlayer(gameIdentity: GameIdentity, playerIdentity: PlayerIdentity, card: Card): Outcome {
        val cmd = AddCardToPlayer(playerIdentity, gameIdentity, card)
        return executeCommand(cmd)
    }

    override suspend fun activatePlayer(playerIdentity: PlayerIdentity, gameIdentity: GameIdentity, teamMateId: PlayerIdentity?): Outcome {
        val cmd = ActivatePlayer(playerIdentity, gameIdentity, teamMateId)
        return executeCommand(cmd)
    }

    override suspend fun setPlayerWaiting(playerIdentity: PlayerIdentity, gameIdentity: GameIdentity, teamMateId: PlayerIdentity?): Outcome {
        val cmd = SetPlayerWaiting(playerIdentity, gameIdentity, teamMateId)
        return executeCommand(cmd)
    }

    private suspend fun executeCommand(command: ICommand<Player>): Outcome {
        val result = playerCommandHandler.handle(command)
        return when {
            result.isSuccess -> Validated.Valid(
                DomainResult(
                    result.getOrThrow().uncommittedEvents(),
                    result.getOrThrow()
                )
            )

            result.isFailure -> Validated.Invalid(DomainError.ExceptionError(result.exceptionOrNull()!!))
            else -> Validated.Invalid(DomainError.ExceptionError(IllegalStateException("Unexpected result state")))
        }
    }
}