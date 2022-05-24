package com.abaddon83.burraco.dealer.ports

import com.abaddon83.burraco.dealer.models.DealerIdentity
import com.abaddon83.burraco.dealer.models.GameIdentity
import com.abaddon83.burraco.dealer.models.PlayerIdentity
import com.abaddon83.burraco.dealer.services.DealerService

sealed class CommandPortResult<out TDomainError: DomainError, out DomainResult> {
    data class Invalid<TDomainError: DomainError>(val err: TDomainError): CommandPortResult<TDomainError, Nothing>()
    data class Valid<DomainResult>(val value: DomainResult): CommandPortResult<Nothing, DomainResult>()
}

interface CommandPort {
    val dealerService: DealerService

    suspend fun createDeck(dealerIdentity: DealerIdentity,gameIdentity: GameIdentity, players: List<PlayerIdentity>): CommandPortResult<DomainError,DomainResult> =
        CommandPortResult.Invalid(DealError("This command is not available through this interface"))


}