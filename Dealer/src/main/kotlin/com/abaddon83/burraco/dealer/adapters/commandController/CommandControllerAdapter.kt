package com.abaddon83.burraco.dealer.adapters.commandController

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.dealer.models.Dealer
import com.abaddon83.burraco.dealer.models.DealerIdentity
import com.abaddon83.burraco.dealer.ports.CommandControllerPort
import com.abaddon83.burraco.dealer.ports.CommandPortResult
import com.abaddon83.burraco.dealer.ports.DomainError
import com.abaddon83.burraco.dealer.ports.DomainResult
import com.abaddon83.burraco.dealer.services.DealerService
import io.github.abaddon.kcqrs.core.domain.SimpleAggregateCommandHandler
import io.github.abaddon.kcqrs.core.persistence.InMemoryEventStoreRepository

class CommandControllerAdapter() : CommandControllerPort {
    override val dealerService: DealerService = DealerService(SimpleAggregateCommandHandler(InMemoryEventStoreRepository("",{ Dealer.empty()})))

    //TODO to fix
    override suspend fun createDeck(
        dealerIdentity: DealerIdentity,
        gameIdentity: GameIdentity,
        players: List<PlayerIdentity>
    ): CommandPortResult<DomainError, DomainResult> {
        dealerService.dealCards(dealerIdentity, gameIdentity, players)
        return CommandPortResult.Valid(DomainResult(listOf(), Dealer.empty()))
    }
}