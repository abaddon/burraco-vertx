package com.abaddon83.vertx.burraco.engine.models.burracoGameWaitingPlayers

import com.abaddon83.burraco.common.events.CardsDealtToPlayer
import com.abaddon83.utils.ddd.Event
import com.abaddon83.burraco.common.events.GameStarted
import com.abaddon83.burraco.common.events.PlayerAdded
import com.abaddon83.vertx.burraco.engine.models.*
import com.abaddon83.vertx.burraco.engine.models.burracoGameExecutions.BurracoGameExecutionTurnBeginning
import com.abaddon83.vertx.burraco.engine.models.burracoGameExecutions.playerInGames.PlayerInGame
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.utils.ddd.writeModel.UnsupportedEventException


data class BurracoGameWaitingPlayers constructor(
        override val identity: GameIdentity,
        override val players: List<BurracoPlayer>,
        val burracoDeck: BurracoDeck) : BurracoGame(identity,"BurracoGameWaitingPlayers") {

    fun addPlayer(player: BurracoPlayer): BurracoGameWaitingPlayers {
        check(players.size < maxPlayers) {
            warnMsg("Maximum number of players reached, (Max: ${maxPlayers})")
        }
        check(!isAlreadyAPlayer(player.identity())) {
            warnMsg("The player ${player.identity()} is already a player of game ${this.identity()}")
        }
        return applyAndQueueEvent(PlayerAdded(identity = identity, playerIdentity = player.identity()))
    }

    fun isAlreadyAPlayer(playerIdentity: PlayerIdentity): Boolean {
        return players.find { p -> p.identity() == playerIdentity } != null
    }

    fun start(): BurracoGameExecutionTurnBeginning {
        check(players.size > 1) {
            warnMsg("Not enough players to initiate the game, ( Min: ${minPlayers})")
        }
        val burracoDealer = BurracoDealer.create(deck, players)

        val eventsToApply: MutableList<Event> =burracoDealer.dealCardsToPlayers.map { (player,cards) ->
            CardsDealtToPlayer(identity = identity(), player = player, cards = cards)
        }.toMutableList()

        eventsToApply.add(GameStarted(
                identity = identity(),
                deck = burracoDealer.burracoDeck.cards,
                //players = burracoDealer.dealCardsToPlayers.map { (player,cards) -> Player(player,cards) },
                mazzettoDeck1 = burracoDealer.dealCardsToFirstMazzetto.getCardList(),
                mazzettoDeck2 = burracoDealer.dealCardsToSecondMazzetto.getCardList(),
                discardPileCards = burracoDealer.dealCardToDiscardPile.showCards(),
                playerTurn = players[0].identity())
        )

        return applyAndQueueEvents(eventsToApply)
    }

    override fun applyEvent(event: Event): BurracoGame {
        log.info("apply event: ${event::class.simpleName.toString()}")
        return when (event) {
            is GameStarted -> apply(event)
            is PlayerAdded -> apply(event)
            is CardsDealtToPlayer -> apply(event)
            else -> throw UnsupportedEventException(event::class.java)
        }
    }


    private fun apply(event: PlayerAdded): BurracoGameWaitingPlayers {
        return copy(players = players.plus(BurracoPlayer(event.playerIdentity)))
    }

    private fun apply(event: CardsDealtToPlayer): BurracoGameWaitingPlayers {
        check(event.identity == identity){ "Game Identity mismatch" }
        val updatedPlayers = players.map { player ->
            when (player.identity()) {
                event.player -> player.copy(cards = event.cards)
                else -> player
            }
        }
        check(updatedPlayers != players)
        return copy(players = updatedPlayers)
    }


    private fun apply(event: GameStarted): BurracoGameExecutionTurnBeginning {
        check(players.filter{ burracoPlayer -> burracoPlayer.cards.isEmpty() }.count() == 0) { "All players have to have their cards" }
        check(event.identity == identity){ "Game Identity mismatch" }

        val gameUpdated = BurracoGameExecutionTurnBeginning.create(
                identity = identity(),
                players = players.map { p -> PlayerInGame.create(p.identity(),p.cards) },
                burracoDeck = BurracoDeck.create(event.deck),
                mazzettoDecks = MazzettoDecks.create(listOf(
                        MazzettoDeck.create(event.mazzettoDeck1),
                        MazzettoDeck.create(event.mazzettoDeck2)
                )),
                discardPile = DiscardPile.create(event.discardPileCards),
                playerTurn = event.playerTurn
        )
        gameUpdated.testInvariants()
        return gameUpdated
    }

}
