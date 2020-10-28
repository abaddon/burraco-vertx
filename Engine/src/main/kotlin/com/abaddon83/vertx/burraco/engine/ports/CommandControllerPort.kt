package com.abaddon83.vertx.burraco.engine.ports

import com.abaddon83.vertx.burraco.engine.commands.CommandHandler
import com.abaddon83.vertx.burraco.engine.commands.DomainError
import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.utils.functionals.Validated


typealias Outcome = Validated<DomainError, OutcomeDetail>
typealias OutcomeDetail = Map<String,String>

interface CommandControllerPort {

    val eventStore: EventStorePort

    val commandHandle: CommandHandler
        get() = CommandHandler(eventStore)

    fun createNewBurracoGame(gameIdentity: GameIdentity): Outcome
    fun joinPlayer(burracoGameIdentity: GameIdentity, playerIdentity: PlayerIdentity): Outcome
    fun startGame(burracoGameIdentity: GameIdentity, playerIdentity: PlayerIdentity): Outcome
    fun pickUpCardFromDeck(burracoGameIdentity: GameIdentity, playerIdentity: PlayerIdentity): Outcome
    fun dropCardOnDiscardPile(burracoGameIdentity: GameIdentity, playerIdentity: PlayerIdentity, cardToDrop: Card): Outcome
}