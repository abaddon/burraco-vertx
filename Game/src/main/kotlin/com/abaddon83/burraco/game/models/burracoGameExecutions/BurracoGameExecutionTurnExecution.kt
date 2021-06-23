package com.abaddon83.burraco.game.models.burracoGameExecutions

import com.abaddon83.utils.ddd.Event
import com.abaddon83.burraco.common.events.*
import com.abaddon83.burraco.game.models.*
import com.abaddon83.burraco.game.models.burracoGameExecutions.playerInGames.PlayerInGame
import com.abaddon83.burraco.common.models.identities.BurracoIdentity
import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.burraco.common.models.entities.BurracoScale
import com.abaddon83.burraco.common.models.entities.BurracoTris
import com.abaddon83.utils.ddd.writeModel.UnsupportedEventException

data class BurracoGameExecutionTurnExecution private constructor(
        override val players: List<PlayerInGame>,
        override val playerTurn: PlayerIdentity,
        override val burracoDeck: BurracoDeck,
        override val playerDeck1: PlayerDeck,
        override val playerDeck2: PlayerDeck,
        override val discardPile: DiscardPile,
        override val identity: GameIdentity) : BurracoGameExecution(identity,"BurracoGameExecutionTurnExecution") {

    companion object Factory {
        fun create(identity: GameIdentity, players: List<PlayerInGame>, burracoDeck: BurracoDeck, playerDeck1: PlayerDeck, playerDeck2: PlayerDeck, discardPile: DiscardPile, playerTurn: PlayerIdentity): BurracoGameExecutionTurnExecution {
            val game = BurracoGameExecutionTurnExecution(
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

    fun dropOnTableATris(playerIdentity: PlayerIdentity, tris: BurracoTris): BurracoGameExecutionTurnExecution {
        val player = validatePlayerId(playerIdentity)
        validatePlayerTurn(playerIdentity)
        player.dropATris(tris)
        return applyAndQueueEvent(
                TrisDropped(identity = identity(), playerIdentity = player.identity(), tris = tris)
        )
    }

    fun dropOnTableAScale(playerIdentity: PlayerIdentity, scale: BurracoScale): BurracoGameExecutionTurnExecution {
        val player = validatePlayerId(playerIdentity)
        validatePlayerTurn(playerIdentity)
        player.dropAScale(scale)
        return applyAndQueueEvent(
                ScaleDropped(identity = identity(), playerIdentity = player.identity(), scale = scale)
        )
    }

    fun appendCardsOnABurracoDropped(playerIdentity: PlayerIdentity, cardsToAppend: List<Card>, burracoIdentity: BurracoIdentity): BurracoGameExecutionTurnExecution {
        val player = validatePlayerId(playerIdentity)
        validatePlayerTurn(playerIdentity)
        player.appendACardOnBurracoDropped(burracoIdentity, cardsToAppend)
        return applyAndQueueEvent(
                CardAddedOnBurraco(identity = identity(), playerIdentity = player.identity(), cardsToAppend = cardsToAppend, burracoIdentity = burracoIdentity)
        )
    }

    fun pickupMazzetto(playerIdentity: PlayerIdentity): BurracoGameExecutionTurnExecution {
        val player = validatePlayerId(playerIdentity)
        validatePlayerTurn(playerIdentity)
        log.debug("show cards: ${player.showMyCards()}")
        check(player.showMyCards().isEmpty()) { warnMsg("The player cannot pick up a Mazzetto if he still has cards ( num cards: ${player.showMyCards().size})") }
        check(!player.isMazzettoTaken()) { warnMsg("The player cannot pick up a Mazzetto he already taken") }

        val mazzetto = firstMazzettoAvailable()

        return applyAndQueueEvent(
                MazzettoPickedUp(identity = identity(), playerIdentity = player.identity(), mazzettoDeck = mazzetto.getCardList())
        )
    }

    fun dropCardOnDiscardPile(playerIdentity: PlayerIdentity, card: Card): BurracoGameExecutionTurnEnd {
        val player = validatePlayerId(playerIdentity)
        validatePlayerTurn(playerIdentity)
        val eventCardDroppedIntoDiscardPile = CardDroppedIntoDiscardPile(identity,player.identity(),card = card)
        return applyAndQueueEvent(eventCardDroppedIntoDiscardPile)
    }

    override fun applyEvent(event: Event): BurracoGame {
        log.info("apply event: ${event::class.simpleName.toString()}")
        return when (event) {
            is CardDroppedIntoDiscardPile -> apply(event)
            is TrisDropped -> apply(event)
            is ScaleDropped -> apply(event)
            is CardAddedOnBurraco -> apply(event)
            is MazzettoPickedUp -> apply(event)
            else -> throw UnsupportedEventException(event::class.java)
        }
    }

    private fun apply(event: CardDroppedIntoDiscardPile): BurracoGameExecutionTurnEnd {
        val player = players.find { p -> p.identity() == event.playerIdentity }!!
        return BurracoGameExecutionTurnEnd.create(
                identity = identity(),
                players = UpdatePlayers(player.dropACard(event.card)),
                playerTurn = playerTurn,
                burracoDeck = burracoDeck,
                playerDeck1 = playerDeck1,
                playerDeck2 = playerDeck2,
                discardPile = discardPile.addCard(event.card)
        )
    }

    private fun apply(event: TrisDropped): BurracoGameExecutionTurnExecution {
        val player = players.find { p -> p.identity() == event.playerIdentity }!!
        return copy(
                players = UpdatePlayers(player.dropATris(event.tris))
        )
    }

    private fun apply(event: ScaleDropped): BurracoGameExecutionTurnExecution {
        val player = players.find { p -> p.identity() == event.playerIdentity }!!
        return copy(
                players = UpdatePlayers(player.dropAScale(event.scale))
        )
    }

    private fun apply(event: CardAddedOnBurraco): BurracoGameExecutionTurnExecution {
        val player = players.find { p -> p.identity() == event.playerIdentity }!!
        return copy(
                players = UpdatePlayers(player.appendACardOnBurracoDropped(event.burracoIdentity, event.cardsToAppend))
        )
    }

    private fun apply(event: MazzettoPickedUp): BurracoGameExecutionTurnExecution {
        val player = players.find { p -> p.identity() == event.playerIdentity }!!
        val mazzettoDeck = PlayerDeck.create(event.mazzettoDeck)
        return copy(
                players = UpdatePlayers(player.pickUpMazzetto(mazzettoDeck)),
                //mazzettoDecks = mazzettoDecks.mazzettoTaken(mazzettoDeck)
        )
    }


}