package com.abaddon83.burraco.dealer.ports

import com.abaddon83.burraco.dealer.models.Dealer
import io.github.abaddon.kcqrs.core.domain.IAggregateCommandHandler

sealed class CommandPortResult<out TDomainError: DomainError, out DomainResult> {
    data class Invalid<TDomainError: DomainError>(val err: TDomainError): CommandPortResult<TDomainError, Nothing>()
    data class Valid<DomainResult>(val value: DomainResult): CommandPortResult<Nothing, DomainResult>()
}

interface CommandPort {
    val dealerCommandHandler: IAggregateCommandHandler<Dealer>

    suspend fun createDeck(): CommandPortResult<DomainError,DomainResult> =
        CommandPortResult.Invalid(DealError("This command is not available through this interface"))


}