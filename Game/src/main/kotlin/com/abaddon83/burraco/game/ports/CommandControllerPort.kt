package com.abaddon83.burraco.game.ports

import com.abaddon83.burraco.common.helpers.Validated
import com.abaddon83.burraco.game.DomainError
import com.abaddon83.burraco.game.DomainResult
import com.abaddon83.burraco.common.models.card.Card
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import io.github.abaddon.kcqrs.core.domain.IAggregateCommandHandler


typealias Outcome = Validated<DomainError, DomainResult>

interface CommandControllerPort {
    val gameCommandHandler: IAggregateCommandHandler<Game>

    suspend fun createGame(): Outcome

    suspend fun addPlayer(gameIdentity: GameIdentity, playerIdentity: PlayerIdentity): Outcome

    suspend fun requestDealCards(gameIdentity: GameIdentity, playerIdentity: PlayerIdentity): Outcome

    suspend fun addCardPlayer(gameIdentity: GameIdentity, playerIdentity: PlayerIdentity, card: Card): Outcome

    suspend fun addCardFirstPlayerDeck(gameIdentity: GameIdentity, card: Card): Outcome

    suspend fun addCardSecondPlayerDeck(gameIdentity: GameIdentity, card: Card): Outcome

    suspend fun addCardDiscardDeck(gameIdentity: GameIdentity, card: Card): Outcome

    suspend fun addCardDeck(gameIdentity: GameIdentity, card: Card): Outcome

    suspend fun startPlayerTurn(gameIdentity: GameIdentity): Outcome

    suspend fun startGame(gameIdentity: GameIdentity): Outcome

}