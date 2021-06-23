package com.abaddon83.burraco.game.ports

import com.abaddon83.burraco.game.commands.DomainError
import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.utils.functionals.Validated
import com.abaddon83.burraco.game.commands.DomainResult
import io.vertx.core.Promise


typealias Outcome = Validated<DomainError, DomainResult>

interface CommandControllerPort {

    val eventStore: EventStorePort

    fun createNewBurracoGame(gameIdentity: GameIdentity): Promise<Outcome>{
        val promise = Promise.promise<Outcome>()
        promise.fail(NotImplementedError("This command is not available through this interface"))
        return promise
    }
    fun addPlayer(burracoGameIdentity: GameIdentity, playerIdentity: PlayerIdentity): Promise<Outcome>{
        val promise = Promise.promise<Outcome>()
        promise.fail(NotImplementedError("This command is not available through this interface"))
        return promise
    }
    fun initGame(burracoGameIdentity: GameIdentity, playerIdentity: PlayerIdentity): Promise<Outcome>{
        val promise = Promise.promise<Outcome>()
        promise.fail(NotImplementedError("This command is not available through this interface"))
        return promise
    }
    fun startGame(burracoGameIdentity: GameIdentity): Promise<Outcome>{
        val promise = Promise.promise<Outcome>()
        promise.fail(NotImplementedError("This command is not available through this interface"))
        return promise
    }
    fun pickUpCardFromDeck(burracoGameIdentity: GameIdentity, playerIdentity: PlayerIdentity): Promise<Outcome>{
        val promise = Promise.promise<Outcome>()
        promise.fail(NotImplementedError("This command is not available through this interface"))
        return promise
    }
    fun dropCardOnDiscardPile(burracoGameIdentity: GameIdentity, playerIdentity: PlayerIdentity, cardToDrop: Card): Promise<Outcome>{
        val promise = Promise.promise<Outcome>()
        promise.fail(NotImplementedError("This command is not available through this interface"))
        return promise
    }

    fun applyCardToPlayer(identity: GameIdentity, playerIdentity: PlayerIdentity, card: Card): Promise<Outcome>{
        val promise = Promise.promise<Outcome>()
        promise.fail(NotImplementedError("This command is not available through this interface"))
        return promise
    }
    fun applyCardToPlayerDeck(identity: GameIdentity, playerDeckId: Int, card: Card): Promise<Outcome>{
        val promise = Promise.promise<Outcome>()
        promise.fail(NotImplementedError("This command is not available through this interface"))
        return promise
    }
    fun applyCardToDiscardDeck(identity: GameIdentity, card: Card): Promise<Outcome>{
        val promise = Promise.promise<Outcome>()
        promise.fail(NotImplementedError("This command is not available through this interface"))
        return promise
    }
    fun applyCardToDeck(identity: GameIdentity, card: Card): Promise<Outcome>{
        val promise = Promise.promise<Outcome>()
        promise.fail(NotImplementedError("This command is not available through this interface"))
        return promise
    }
}