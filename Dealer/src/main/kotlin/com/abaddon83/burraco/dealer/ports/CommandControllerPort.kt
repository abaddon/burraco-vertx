package com.abaddon83.burraco.dealer.ports

import com.abaddon83.burraco.common.helpers.Validated
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.dealer.DomainError
import com.abaddon83.burraco.dealer.DomainResult
import com.abaddon83.burraco.dealer.models.DealerIdentity


typealias Outcome = Validated<DomainError, DomainResult>

interface CommandControllerPort {
    suspend fun createDecks(dealerIdentity: DealerIdentity, gameIdentity: GameIdentity, players: List<PlayerIdentity>): Outcome

    suspend fun dealCardToDeck(dealerIdentity: DealerIdentity,gameIdentity: GameIdentity): Outcome

    suspend fun dealCardToDiscardDeck(dealerIdentity: DealerIdentity,gameIdentity: GameIdentity): Outcome

    suspend fun dealCardToPlayer(dealerIdentity: DealerIdentity,gameIdentity: GameIdentity, playerIdentity: PlayerIdentity): Outcome

    suspend fun dealCardToPlayerDeck1(dealerIdentity: DealerIdentity,gameIdentity: GameIdentity): Outcome

    suspend fun dealCardToPlayerDeck2(dealerIdentity: DealerIdentity,gameIdentity: GameIdentity): Outcome

}