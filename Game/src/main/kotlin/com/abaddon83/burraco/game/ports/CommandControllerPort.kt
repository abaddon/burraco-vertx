package com.abaddon83.burraco.game.ports

import com.abaddon83.burraco.game.DomainError
import com.abaddon83.burraco.game.DomainResult
import com.abaddon83.burraco.game.helpers.Validated
import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameIdentity
import com.abaddon83.burraco.game.models.player.PlayerIdentity
import io.github.abaddon.kcqrs.core.domain.IAggregateCommandHandler
import io.vertx.core.Promise


typealias Outcome = Validated<DomainError, DomainResult>

interface CommandControllerPort: IAggregateCommandHandler<Game> {

    //val eventStore: EventStorePort

//    fun createGame(gameIdentity: GameIdentity): Promise<Outcome>{
//        val promise = Promise.promise<Outcome>()
//        promise.fail(NotImplementedError("This command is not available through this interface"))
//        return promise
//    }
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