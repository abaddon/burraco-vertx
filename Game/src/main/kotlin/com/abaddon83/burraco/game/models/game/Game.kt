package com.abaddon83.burraco.game.models.game

import com.abaddon83.burraco.game.models.player.Player
import io.github.abaddon.kcqrs.core.domain.AggregateRoot
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import org.slf4j.Logger

abstract class Game() : AggregateRoot() {
    protected abstract val log: Logger
    abstract val players: List<Player>
    override val uncommittedEvents: MutableCollection<IDomainEvent> = mutableListOf()


//    companion object Factory {
//        fun create(gameIdentity: GameIdentity): BurracoGameWaitingPlayers {
//            //TODO generate cards
//            //val listCards=BurracoDeck.create().cards
//            return Game(gameIdentity)
//                    .applyAndQueueEvent(BurracoGameCreated(gameIdentity))
//        }
//    }


//    override fun applyEvent(event: Event): Game {
//        log.info("apply event: ${event::class.simpleName.toString()}")
//        return when (event) {
//            is BurracoGameCreated -> apply(event)
//            else -> throw UnsupportedEventException(event::class.java)
//        }
//    }
//    private fun apply(event: BurracoGameCreated):BurracoGameWaitingPlayers {
//        //val burracoDeck = BurracoDeck.create(event.deck)
//        return BurracoGameWaitingPlayers(event.identity, listOf()/*, burracoDeck*/)
//    }
//
//    protected fun sizePlayerDeck(playerDeckId: Int): Int{
//        return when(playerDeckId){
//            0 -> if (players.size > 2) 11 else 13
//            1 -> 11
//            else -> throw Exception("Player deck id has to be 0 or 1")
//        }
//    }
//
//    protected fun initialSizeDeck(): Int {
//        val playerDeck0Size = sizePlayerDeck(0)
//        val playerDeck1Size = sizePlayerDeck(1)
//        val playersCards = 11 * players.size
//        return totalCardsRequired - playerDeck0Size - playerDeck1Size - playersCards - initialDiscardDeckSize
//    }
}