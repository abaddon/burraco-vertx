package com.abaddon83.burraco.dealer.ports

import com.abaddon83.burraco.dealer.helpers.Validated
import com.abaddon83.burraco.dealer.models.Dealer
import io.github.abaddon.kcqrs.core.domain.IAggregateCommandHandler

typealias Outcome = Validated<DomainError, DomainResult>

interface CommandControllerPort {
    val dealerCommandHandler: IAggregateCommandHandler<Dealer>

    suspend fun createDeck(): Outcome =
        Validated.Invalid(DealError("This command is not available through this interface"))


}