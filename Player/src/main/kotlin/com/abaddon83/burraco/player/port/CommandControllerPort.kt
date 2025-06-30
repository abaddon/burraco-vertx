package com.abaddon83.burraco.player.port

import com.abaddon83.burraco.common.helpers.Validated
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.player.DomainError
import com.abaddon83.burraco.player.DomainResult
import com.abaddon83.burraco.player.model.player.Player
import io.github.abaddon.kcqrs.core.domain.IAggregateCommandHandler


typealias Outcome = Validated<DomainError, DomainResult>

interface CommandControllerPort {
    val playerCommandHandler: IAggregateCommandHandler<Player>

    suspend fun createPlayer(gameIdentity: GameIdentity, user: String): Outcome

    suspend fun deletePlayer(playerIdentity: PlayerIdentity): Outcome

//
//    suspend fun requestDealCards(gameIdentity: GameIdentity, playerIdentity: PlayerIdentity): Outcome
//
//    suspend fun addCardPlayer(gameIdentity: GameIdentity, playerIdentity: PlayerIdentity, card: Card): Outcome
//
//    suspend fun addCardFirstPlayerDeck(gameIdentity: GameIdentity, card: Card): Outcome
//
//    suspend fun addCardSecondPlayerDeck(gameIdentity: GameIdentity, card: Card): Outcome
//
//    suspend fun addCardDiscardDeck(gameIdentity: GameIdentity, card: Card): Outcome
//
//    suspend fun addCardDeck(gameIdentity: GameIdentity, card: Card): Outcome
//
//    suspend fun startPlayerTurn(gameIdentity: GameIdentity): Outcome

}