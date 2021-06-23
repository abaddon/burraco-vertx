package com.abaddon83.burraco.game.models

import com.abaddon83.burraco.common.events.BurracoGameCreated
import com.abaddon83.utils.ddd.Event
import com.abaddon83.burraco.game.models.burracoGameWaitingPlayers.BurracoGameWaitingPlayers
import com.abaddon83.burraco.game.models.games.Game
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.utils.ddd.writeModel.AggregateRoot
import com.abaddon83.utils.ddd.writeModel.AggregateType
import com.abaddon83.utils.ddd.writeModel.UnsupportedEventException
import com.abaddon83.burraco.game.models.players.Player

open class BurracoGame(override val identity: GameIdentity, className:String) : Game, AggregateRoot<GameIdentity>(className) {
    constructor(identity: GameIdentity): this(identity,"BurracoGame")
    override val maxPlayers: Int = 4
    override val minPlayers: Int = 2
    override val totalCardsRequired: Int = 108
    override val numInitialPlayerCards: Int = 11
    override val players: List<Player> =listOf()
    override val deck: BurracoDeck = BurracoDeck.create(listOf())
    protected val initialDiscardDeckSize: Int = 1

    object TYPE : AggregateType {
        override fun toString() = "BurracoGame"
    }
    override fun aggregateType(): AggregateType = TYPE

    override fun listOfPlayers(): List<Player> = players

    companion object Factory {
        fun create(gameIdentity: GameIdentity): BurracoGameWaitingPlayers {
            //TODO generate cards
            //val listCards=BurracoDeck.create().cards
            return BurracoGame(gameIdentity)
                    .applyAndQueueEvent(BurracoGameCreated(gameIdentity))
        }
    }


    override fun applyEvent(event: Event): BurracoGame {
        log.info("apply event: ${event::class.simpleName.toString()}")
        return when (event) {
            is BurracoGameCreated -> apply(event)
            else -> throw UnsupportedEventException(event::class.java)
        }
    }
    private fun apply(event: BurracoGameCreated):BurracoGameWaitingPlayers {
        //val burracoDeck = BurracoDeck.create(event.deck)
        return BurracoGameWaitingPlayers(event.identity, listOf()/*, burracoDeck*/)
    }

    protected fun sizePlayerDeck(playerDeckId: Int): Int{
        return when(playerDeckId){
            0 -> if (players.size > 2) 11 else 13
            1 -> 11
            else -> throw Exception("Player deck id has to be 0 or 1")
        }
    }

    protected fun initialSizeDeck(): Int {
        val playerDeck0Size = sizePlayerDeck(0)
        val playerDeck1Size = sizePlayerDeck(1)
        val playersCards = 11 * players.size
        return totalCardsRequired - playerDeck0Size - playerDeck1Size - playersCards - initialDiscardDeckSize
    }
}