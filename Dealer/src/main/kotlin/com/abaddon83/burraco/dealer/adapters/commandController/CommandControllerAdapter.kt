package com.abaddon83.burraco.dealer.adapters.commandController

import com.abaddon83.burraco.common.helpers.Validated
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.dealer.DomainResult
import com.abaddon83.burraco.dealer.ExceptionError
import com.abaddon83.burraco.dealer.commands.*
import com.abaddon83.burraco.dealer.models.Dealer
import com.abaddon83.burraco.common.models.DealerIdentity
import com.abaddon83.burraco.dealer.ports.CommandControllerPort
import com.abaddon83.burraco.dealer.ports.Outcome
import io.github.abaddon.kcqrs.core.domain.IAggregateCommandHandler
import io.github.abaddon.kcqrs.core.domain.messages.commands.ICommand
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class CommandControllerAdapter(
    private val dealerCommandHandler: IAggregateCommandHandler<Dealer>,
    private val coroutineContext: CoroutineContext,
) : CommandControllerPort {


    override suspend fun createDecks(
        dealerIdentity: DealerIdentity,
        gameIdentity: GameIdentity,
        players: List<PlayerIdentity>
    ): Outcome = withContext(coroutineContext) {
        val cmd = CreateDecks(dealerIdentity, gameIdentity, players)
        executeCommand(cmd)
    }

    override suspend fun dealCardToDeck(dealerIdentity: DealerIdentity, gameIdentity: GameIdentity): Outcome =
        withContext(coroutineContext) {
            val cmd = DealCardToDeck(dealerIdentity, gameIdentity)
            executeCommand(cmd)
        }

    override suspend fun dealCardToDiscardDeck(dealerIdentity: DealerIdentity, gameIdentity: GameIdentity): Outcome =
        withContext(coroutineContext) {
            val cmd = DealCardToDiscardDeck(dealerIdentity, gameIdentity)
            executeCommand(cmd)
        }

    override suspend fun dealCardToPlayer(
        dealerIdentity: DealerIdentity,
        gameIdentity: GameIdentity,
        playerIdentity: PlayerIdentity
    ): Outcome = withContext(coroutineContext) {
        val cmd = DealCardToPlayer(dealerIdentity, gameIdentity, playerIdentity)
        executeCommand(cmd)
    }

    override suspend fun dealCardToPlayerDeck1(dealerIdentity: DealerIdentity, gameIdentity: GameIdentity): Outcome =
        withContext(coroutineContext) {
            val cmd = DealCardToPlayerDeck1(dealerIdentity, gameIdentity)
            executeCommand(cmd)
        }

    override suspend fun dealCardToPlayerDeck2(dealerIdentity: DealerIdentity, gameIdentity: GameIdentity): Outcome =
        withContext(coroutineContext) {
            val cmd = DealCardToPlayerDeck2(dealerIdentity, gameIdentity)
            executeCommand(cmd)
        }

    private suspend fun executeCommand(command: ICommand<Dealer>): Outcome = withContext(coroutineContext) {
        val actualResult = dealerCommandHandler.handle(command)

        if (actualResult.isSuccess) {
            Validated.Valid(DomainResult(actualResult.getOrThrow().uncommittedEvents(), actualResult.getOrThrow()))
        } else {
            Validated.Invalid(ExceptionError(Exception(actualResult.exceptionOrNull())))
        }
    }

}