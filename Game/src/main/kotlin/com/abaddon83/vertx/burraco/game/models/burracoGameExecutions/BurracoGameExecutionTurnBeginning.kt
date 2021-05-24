package com.abaddon83.vertx.burraco.game.models.burracoGameExecutions

import com.abaddon83.utils.ddd.Event
import com.abaddon83.burraco.common.events.CardPickedFromDeck
import com.abaddon83.burraco.common.events.CardsPickedFromDiscardPile
import com.abaddon83.vertx.burraco.game.models.burracoGameExecutions.playerInGames.PlayerInGame
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.utils.ddd.writeModel.UnsupportedEventException
import com.abaddon83.vertx.burraco.game.models.*

data class BurracoGameExecutionTurnBeginning private constructor(
    override val identity: GameIdentity,
    override val players: List<PlayerInGame>,
    override val playerTurn: PlayerIdentity,
    override val burracoDeck: BurracoDeck,
    override val playerDeck1: PlayerDeck,
    override val playerDeck2: PlayerDeck,
    override val discardPile: DiscardPile
) : BurracoGameExecution(identity, "BurracoGameExecutionTurnBeginning") {

    companion object Factory {
        fun create(
            identity: GameIdentity,
            players: List<PlayerInGame>,
            burracoDeck: BurracoDeck,
            playerDeck1: PlayerDeck,
            playerDeck2: PlayerDeck,
            discardPile: DiscardPile,
            playerTurn: PlayerIdentity
        ): BurracoGameExecutionTurnBeginning {
            val game = BurracoGameExecutionTurnBeginning(
                identity = identity,
                players = players,
                burracoDeck = burracoDeck,
                playerDeck1 = playerDeck1,
                playerDeck2 = playerDeck2,
                discardPile = discardPile,
                playerTurn = playerTurn
            )
            game.testInvariants()
            return game
        }

    }

    //When the turn start the player can pickUp a card from the Deck
    fun pickUpACardFromDeck(playerIdentity: PlayerIdentity): BurracoGameExecutionTurnExecution {
        val player = validatePlayerId(playerIdentity)
        validatePlayerTurn(playerIdentity)
        return applyAndQueueEvent(CardPickedFromDeck(identity, player.identity(), burracoDeck.getFirstCard()))
    }

    //When the turn start the player can pickUp all cards from the DiscardPile if it's not empty
    fun pickUpCardsFromDiscardPile(playerIdentity: PlayerIdentity): BurracoGameExecutionTurnExecution {
        val player = validatePlayerId(playerIdentity)
        validatePlayerTurn(playerIdentity)
        return applyAndQueueEvent(CardsPickedFromDiscardPile(identity, player.identity(), discardPile.grabAllCards()))
    }

    override fun applyEvent(event: Event): BurracoGame {
        log.info("apply event: ${event::class.simpleName.toString()}")
        return when (event) {
            is CardPickedFromDeck -> apply(event)
            is CardsPickedFromDiscardPile -> apply(event)
            else -> throw UnsupportedEventException(event::class.java)
        }
    }

    private fun apply(event: CardPickedFromDeck): BurracoGameExecutionTurnExecution {
        val player = players.find { p -> p.identity() == event.playerIdentity }!!

        return BurracoGameExecutionTurnExecution.create(
            identity = this.identity(),
            players = UpdatePlayers(player.addCardsOnMyCard(listOf(event.card))),
            burracoDeck = BurracoDeck.create(this.burracoDeck.cards.minusElement(event.card)),
            playerDeck1 = playerDeck1,
            playerDeck2 = playerDeck2,
            discardPile = this.discardPile,
            playerTurn = this.playerTurn
        )
    }

    private fun apply(event: CardsPickedFromDiscardPile): BurracoGameExecutionTurnExecution {
        val player = players.find { p -> p.identity() == event.playerIdentity }!!

        return BurracoGameExecutionTurnExecution.create(
            identity = this.identity(),
            players = UpdatePlayers(player.addCardsOnMyCard(event.cards)),
            burracoDeck = this.burracoDeck,
            playerDeck1 = playerDeck1,
            playerDeck2 = playerDeck2,
            discardPile = DiscardPile.create(discardPile.showCards().minus(event.cards)),
            playerTurn = this.playerTurn
        )
    }
}