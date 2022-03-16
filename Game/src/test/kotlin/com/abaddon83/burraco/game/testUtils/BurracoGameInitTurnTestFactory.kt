package com.abaddon83.burraco.game.testUtils

import com.abaddon83.burraco.game.models.burracoGameExecutions.playerInGames.PlayerInGame
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.burraco.game.models.*
import com.abaddon83.burraco.game.models.game.GameIdentity

data class BurracoGameInitTurnTestFactory(
    private val gameIdentity: GameIdentity,
    private val player1: PlayerInGame,
    private val player2: PlayerInGame,
    private val mazzettoDecks: MazzettoDecks,
    private val discardPile: DiscardPile,
    private val playerTurn: PlayerIdentity
) {
//
//    fun buildTurnPhaseStart(): BurracoGameExecutionTurnBeginning {
//        return BurracoGameExecutionTurnBeginning.create(
//                identity = gameIdentity,
//                players = listOf(player1, player2),
//                burracoDeck = burracoDeckBuild(),
//                //mazzettoDecks = mazzettoDecks,
//                discardPile = discardPile,
//                playerTurn = playerTurn
//        )
//    }
//
//    fun buildTurnPhaseExecution(): BurracoGameExecutionTurnExecution =
//            BurracoGameExecutionTurnExecution.create(
//                    identity = gameIdentity,
//                    players = listOf(player1, player2),
//                    burracoDeck = burracoDeckBuild(),
//                    mazzettoDecks = mazzettoDecks,
//                    discardPile = discardPile,
//                    playerTurn = playerTurn
//            )
//
//    fun buildTurnPhaseEnd(): BurracoGameExecutionTurnEnd =
//            BurracoGameExecutionTurnEnd.create(
//                    identity = gameIdentity,
//                    players = listOf(player1, player2),
//                    burracoDeck = burracoDeckBuild(),
//                    mazzettoDecks = mazzettoDecks,
//                    discardPile = discardPile,
//                    playerTurn = playerTurn
//            )
//
//    fun buildWaitingPlayer(singlePlayer: Boolean = false): BurracoGameWaitingPlayers =
//            BurracoGameWaitingPlayers(
//                    identity = gameIdentity,
//                    players = when (singlePlayer) {
//                        true -> listOf(BurracoPlayer(player1.identity()) )
//                        false -> listOf(BurracoPlayer(player1.identity()), BurracoPlayer(player2.identity()))
//                    }, burracoDeck = burracoDeckBuild()
//            )
//
//    fun burracoDeckBuild(): BurracoDeck {
//        val doubleDeck = ListCardsBuilder.allRanksWithJollyCards().plus(ListCardsBuilder.allRanksWithJollyCards())
//        val numCardToDrop = discardPile.numCards() + mazzettoDecks.numCards() + player1.totalPlayerCards() + player2.totalPlayerCards()
//        return BurracoDeck.create(doubleDeck.drop(numCardToDrop))
//    }
//
//    fun setPlayer1(player: PlayerInGame): BurracoGameInitTurnTestFactory = this.copy(player1 = player)
//
//    fun setPlayer2Turn(): BurracoGameInitTurnTestFactory = this.copy(playerTurn = player2.identity())
//
//    fun setDiscardPile(list: List<Card>): BurracoGameInitTurnTestFactory = this.copy(discardPile = DiscardPile.create(list))
//
//    companion object Factory{
//        fun create(gameIdentity: GameIdentity = GameIdentity.create(), player1Id: PlayerIdentity = PlayerIdentity.create(), player2Id: PlayerIdentity = PlayerIdentity.create()): BurracoGameInitTurnTestFactory {
//
//            val player1 = PlayerInGame.create(player1Id, ListCardsBuilder.allRanksCards().take(11))
//            val player2 = PlayerInGame.create(player2Id, ListCardsBuilder.allRanksCards().take(11))
//
//            val mazzettoDecks = MazzettoDecks.create(listOf(
//                    PlayerDeck.create(ListCardsBuilder.allRanksCards().take(11)),
//                    PlayerDeck.create(ListCardsBuilder.allRanksCards().take(11))
//            ))
//            val discardPile = DiscardPile.create(listOf())
//
//            return BurracoGameInitTurnTestFactory(
//                    gameIdentity = gameIdentity,
//            player1 = player1,
//            player2 = player2,
//            mazzettoDecks = mazzettoDecks,
//            discardPile = discardPile,
//            playerTurn = player1.identity()
//            )
//        }
//    }
}
