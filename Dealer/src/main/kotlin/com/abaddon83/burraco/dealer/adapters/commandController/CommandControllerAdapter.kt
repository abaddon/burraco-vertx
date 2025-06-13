package com.abaddon83.burraco.dealer.adapters.commandController

import com.abaddon83.burraco.common.helpers.Validated
import com.abaddon83.burraco.common.models.DealerIdentity
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.dealer.DomainResult
import com.abaddon83.burraco.dealer.ExceptionError
import com.abaddon83.burraco.dealer.models.Dealer
import com.abaddon83.burraco.dealer.ports.CommandControllerPort
import com.abaddon83.burraco.dealer.ports.Outcome
import com.abaddon83.burraco.dealer.services.DealerService
import com.abaddon83.burraco.dealer.services.DealerServiceResult
import io.github.abaddon.kcqrs.core.domain.IAggregateCommandHandler
import io.github.abaddon.kcqrs.core.domain.messages.commands.ICommand

class CommandControllerAdapter(
    private val dealerCommandHandler: IAggregateCommandHandler<Dealer>,
) : CommandControllerPort {

    private val dealerService = DealerService(dealerCommandHandler)

    override suspend fun cardRequestedToDealer(
        gameIdentity: GameIdentity,
        players: List<PlayerIdentity>
    ): Outcome {
        val dealerIdentity = DealerIdentity.create()
        val result = dealerService.dealCards(dealerIdentity, gameIdentity, players)
        when (result) {
            is DealerServiceResult.Invalid -> {
                return Validated.Invalid(result.err)
            }

            is DealerServiceResult.Valid<DomainResult> -> {
                return Validated.Valid(result.value)

            }
        }

    }


    private suspend fun executeCommand(command: ICommand<Dealer>): Outcome {
        val actualResult = dealerCommandHandler.handle(command)

        if (actualResult.isSuccess) {
            return Validated.Valid(
                DomainResult(
                    actualResult.getOrThrow().uncommittedEvents(),
                    actualResult.getOrThrow()
                )
            )
        } else {
            return Validated.Invalid(ExceptionError(Exception(actualResult.exceptionOrNull())))
        }
    }

}