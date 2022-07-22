package com.abaddon83.burraco.game.adapters.commandController

import com.abaddon83.burraco.common.helpers.Validated
import com.abaddon83.burraco.game.DomainResult
import com.abaddon83.burraco.game.ExceptionError
import com.abaddon83.burraco.game.commands.gameDraft.AddPlayer
import com.abaddon83.burraco.game.commands.gameDraft.CreateGame
import com.abaddon83.burraco.game.commands.gameDraft.RequestDealCards
import com.abaddon83.burraco.game.commands.gameWaitingDealer.*
import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.game.ports.CommandControllerPort
import com.abaddon83.burraco.game.ports.Outcome
import io.github.abaddon.kcqrs.core.domain.IAggregateCommandHandler
import io.github.abaddon.kcqrs.core.domain.Result
import io.github.abaddon.kcqrs.core.domain.messages.commands.ICommand

class CommandControllerAdapter(override val gameCommandHandler: IAggregateCommandHandler<Game>) :
    CommandControllerPort {

    override suspend fun createGame(): Outcome {
        val cmd= CreateGame(GameIdentity.create())
        return executeCommand(cmd)
    }

    override suspend fun addPlayer(gameIdentity: GameIdentity, playerIdentity: PlayerIdentity): Outcome {
        val cmd= AddPlayer(gameIdentity,playerIdentity)
        return executeCommand(cmd)
    }

    override suspend fun requestDealCards(gameIdentity: GameIdentity, playerIdentity: PlayerIdentity): Outcome {
        val cmd= RequestDealCards(gameIdentity,playerIdentity)
        return executeCommand(cmd)
    }

    override suspend fun addCardPlayer(
        gameIdentity: GameIdentity,
        playerIdentity: PlayerIdentity,
        card: Card
    ): Outcome {
        val cmd= AddCardPlayer(gameIdentity,playerIdentity,card)
        return executeCommand(cmd)
    }

    override suspend fun addCardFirstPlayerDeck(gameIdentity: GameIdentity, card: Card): Outcome {
        val cmd= AddCardFirstPlayerDeck(gameIdentity,card)
        return executeCommand(cmd)
    }

    override suspend fun addCardSecondPlayerDeck(gameIdentity: GameIdentity, card: Card): Outcome {
        val cmd= AddCardSecondPlayerDeck(gameIdentity,card)
        return executeCommand(cmd)
    }

    override suspend fun addCardDiscardDeck(gameIdentity: GameIdentity, card: Card): Outcome {
        val cmd= AddCardDiscardDeck(gameIdentity,card)
        return executeCommand(cmd)
    }

    override suspend fun addCardDeck(gameIdentity: GameIdentity, card: Card): Outcome {
        val cmd= AddCardDeck(gameIdentity,card)
        return executeCommand(cmd)
    }

    override suspend fun startPlayerTurn(gameIdentity: GameIdentity): Outcome {
        val cmd= StartPlayerTurn(gameIdentity)
        return executeCommand(cmd)
    }

    private suspend fun executeCommand(command: ICommand<Game>):Outcome =
        when (val actualResult = gameCommandHandler.handle(command)) {
            is Result.Invalid -> Validated.Invalid(ExceptionError(actualResult.err))
            is Result.Valid -> Validated.Valid(DomainResult(actualResult.value.uncommittedEvents(), actualResult.value))
        }

}