package com.abaddon83.vertx.burraco.game.ports

import com.abaddon83.vertx.burraco.game.commands.CommandHandler
import com.abaddon83.vertx.burraco.game.commands.DomainError
import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.utils.functionals.Validated
import com.abaddon83.vertx.burraco.game.commands.DomainResult


typealias Outcome = Validated<DomainError, DomainResult>

interface CommandControllerPort {

    val eventStore: EventStorePort

    val commandHandle: CommandHandler
        get() = CommandHandler(eventStore)

    fun createNewBurracoGame(gameIdentity: GameIdentity): Outcome
    fun addPlayer(burracoGameIdentity: GameIdentity, playerIdentity: PlayerIdentity): Outcome
    fun startGame(burracoGameIdentity: GameIdentity, playerIdentity: PlayerIdentity): Outcome
    fun pickUpCardFromDeck(burracoGameIdentity: GameIdentity, playerIdentity: PlayerIdentity): Outcome
    fun dropCardOnDiscardPile(burracoGameIdentity: GameIdentity, playerIdentity: PlayerIdentity, cardToDrop: Card): Outcome
}