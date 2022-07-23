package com.abaddon83.burraco.dealer.adapters.commandController

import com.abaddon83.burraco.common.helpers.Validated
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.dealer.models.Dealer
import com.abaddon83.burraco.dealer.models.DealerIdentity
import com.abaddon83.burraco.dealer.ports.CommandControllerPort
import com.abaddon83.burraco.dealer.DomainResult
import com.abaddon83.burraco.dealer.ExceptionError
import com.abaddon83.burraco.dealer.commands.*
import com.abaddon83.burraco.dealer.ports.Outcome
import io.github.abaddon.kcqrs.core.domain.IAggregateCommandHandler
import io.github.abaddon.kcqrs.core.domain.Result
import io.github.abaddon.kcqrs.core.domain.messages.commands.ICommand

class CommandControllerAdapter(private val dealerCommandHandler: IAggregateCommandHandler<Dealer>) : CommandControllerPort {

    override suspend fun createDecks(
        dealerIdentity: DealerIdentity,
        gameIdentity: GameIdentity,
        players: List<PlayerIdentity>
    ): Outcome {
        val cmd= CreateDecks(dealerIdentity,gameIdentity, players)
        return executeCommand(cmd)
    }

    override suspend fun dealCardToDeck(dealerIdentity: DealerIdentity, gameIdentity: GameIdentity): Outcome {
        val cmd= DealCardToDeck(dealerIdentity,gameIdentity)
        return executeCommand(cmd)
    }

    override suspend fun dealCardToDiscardDeck(dealerIdentity: DealerIdentity, gameIdentity: GameIdentity): Outcome {
        val cmd= DealCardToDiscardDeck(dealerIdentity,gameIdentity)
        return executeCommand(cmd)
    }

    override suspend fun dealCardToPlayer(
        dealerIdentity: DealerIdentity,
        gameIdentity: GameIdentity,
        playerIdentity: PlayerIdentity
    ): Outcome {
        val cmd= DealCardToPlayer(dealerIdentity,gameIdentity,playerIdentity)
        return executeCommand(cmd)
    }

    override suspend fun dealCardToPlayerDeck1(dealerIdentity: DealerIdentity, gameIdentity: GameIdentity): Outcome {
        val cmd= DealCardToPlayerDeck1(dealerIdentity,gameIdentity)
        return executeCommand(cmd)
    }

    override suspend fun dealCardToPlayerDeck2(dealerIdentity: DealerIdentity, gameIdentity: GameIdentity): Outcome {
        val cmd= DealCardToPlayerDeck2(dealerIdentity,gameIdentity)
        return executeCommand(cmd)
    }

    private suspend fun executeCommand(command: ICommand<Dealer>):Outcome =
        when (val actualResult = dealerCommandHandler.handle(command)) {
            is Result.Invalid -> Validated.Invalid(ExceptionError(actualResult.err))
            is Result.Valid -> Validated.Valid(DomainResult(actualResult.value.uncommittedEvents(), actualResult.value))
        }
}