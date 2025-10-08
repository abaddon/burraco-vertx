package com.abaddon83.burraco.player.port

import com.abaddon83.burraco.common.helpers.Validated
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.card.Card
import com.abaddon83.burraco.player.DomainError
import com.abaddon83.burraco.player.DomainResult
import com.abaddon83.burraco.player.model.player.Player
import io.github.abaddon.kcqrs.core.domain.IAggregateCommandHandler


typealias Outcome = Validated<DomainError, DomainResult>

interface CommandControllerPort {
    val playerCommandHandler: IAggregateCommandHandler<Player>

    suspend fun createPlayer(gameIdentity: GameIdentity, user: String): Outcome

    suspend fun deletePlayer(playerIdentity: PlayerIdentity): Outcome

    suspend fun addCardToPlayer(gameIdentity: GameIdentity, playerIdentity: PlayerIdentity, card: Card): Outcome

}