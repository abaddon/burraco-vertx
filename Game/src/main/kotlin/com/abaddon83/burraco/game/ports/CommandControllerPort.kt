package com.abaddon83.burraco.game.ports

import com.abaddon83.burraco.game.DomainError
import com.abaddon83.burraco.game.DomainResult
import com.abaddon83.burraco.game.GameError
import com.abaddon83.burraco.game.helpers.Validated
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameIdentity
import io.github.abaddon.kcqrs.core.domain.IAggregateCommandHandler


typealias Outcome = Validated<DomainError, DomainResult>

interface CommandControllerPort {
    val gameCommandHandler: IAggregateCommandHandler<Game>

    suspend fun createGame(): Outcome =
        Validated.Invalid(GameError("This command is not available through this interface"))

//    fun addPlayer(burracoGameIdentity: GameIdentity, playerIdentity: PlayerIdentity): Promise<Outcome>{
//        val promise = Promise.promise<Outcome>()
//        promise.fail(NotImplementedError("This command is not available through this interface"))
//        return promise
//    }
//    fun initGame(burracoGameIdentity: GameIdentity, playerIdentity: PlayerIdentity): Promise<Outcome>{
//        val promise = Promise.promise<Outcome>()
//        promise.fail(NotImplementedError("This command is not available through this interface"))
//        return promise
//    }
//    fun startGame(burracoGameIdentity: GameIdentity): Promise<Outcome>{
//        val promise = Promise.promise<Outcome>()
//        promise.fail(NotImplementedError("This command is not available through this interface"))
//        return promise
//    }
//    fun pickUpCardFromDeck(burracoGameIdentity: GameIdentity, playerIdentity: PlayerIdentity): Promise<Outcome>{
//        val promise = Promise.promise<Outcome>()
//        promise.fail(NotImplementedError("This command is not available through this interface"))
//        return promise
//    }
//    fun dropCardOnDiscardPile(burracoGameIdentity: GameIdentity, playerIdentity: PlayerIdentity, cardToDrop: Card): Promise<Outcome>{
//        val promise = Promise.promise<Outcome>()
//        promise.fail(NotImplementedError("This command is not available through this interface"))
//        return promise
//    }
//
//    fun applyCardToPlayer(identity: GameIdentity, playerIdentity: PlayerIdentity, card: Card): Promise<Outcome>{
//        val promise = Promise.promise<Outcome>()
//        promise.fail(NotImplementedError("This command is not available through this interface"))
//        return promise
//    }
//    fun applyCardToPlayerDeck(identity: GameIdentity, playerDeckId: Int, card: Card): Promise<Outcome>{
//        val promise = Promise.promise<Outcome>()
//        promise.fail(NotImplementedError("This command is not available through this interface"))
//        return promise
//    }
//    fun applyCardToDiscardDeck(identity: GameIdentity, card: Card): Promise<Outcome>{
//        val promise = Promise.promise<Outcome>()
//        promise.fail(NotImplementedError("This command is not available through this interface"))
//        return promise
//    }
//    fun applyCardToDeck(identity: GameIdentity, card: Card): Promise<Outcome>{
//        val promise = Promise.promise<Outcome>()
//        promise.fail(NotImplementedError("This command is not available through this interface"))
//        return promise
//    }
}