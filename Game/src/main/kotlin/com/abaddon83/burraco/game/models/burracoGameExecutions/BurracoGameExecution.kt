package com.abaddon83.burraco.game.models.burracoGameExecutions

import com.abaddon83.burraco.game.models.*
import com.abaddon83.burraco.game.models.burracoGameExecutions.playerInGames.PlayerInGame
import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.burraco.common.models.entities.BurracoScale
import com.abaddon83.burraco.common.models.entities.BurracoTris
import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameIdentity
import com.abaddon83.burraco.game.models.player.PlayerIdentity

abstract class BurracoGameExecution(identity: GameIdentity, classname:String) : Game(identity, classname) {
    abstract override val players: List<PlayerInGame>
    protected abstract val playerTurn: PlayerIdentity
    protected abstract val burracoDeck: BurracoDeck
    protected abstract val playerDeck1: PlayerDeck
    protected abstract val playerDeck2: PlayerDeck
    protected abstract val discardPile: DiscardPile

    //READ Methods
    fun playerCards(playerIdentity: PlayerIdentity): List<Card> =
            when (val player = players.find { p -> p.identity() == playerIdentity }) {
                is PlayerInGame -> player.showMyCards()
                else -> throw NoSuchElementException("Player $playerIdentity is not a player of this game $id")
            }

    fun playerTrisOnTable(playerIdentity: PlayerIdentity): List<BurracoTris> =
            when (val player = players.find { p -> p.identity() == playerIdentity }) {
                is PlayerInGame -> player.showTrisOnTable()
                else -> throw NoSuchElementException("Player $playerIdentity is not a player of this game ${identity()}")
            }

    fun playerScalesOnTable(playerIdentity: PlayerIdentity): List<BurracoScale> =
            when (val player = players.find { p -> p.identity() == playerIdentity }) {
                is PlayerInGame -> player.showScalesOnTable()
                else -> throw NoSuchElementException("Player $playerIdentity is not a player of this game ${identity()}")
            }

    fun showDiscardPile(): List<Card> = discardPile.showCards()

    fun showPlayerTurn(): PlayerIdentity = playerTurn

    fun showNumMazzettoAvailable(): Int {
        return listOf(playerDeck1, playerDeck2).fold(0){ sum, deck -> sum + (if (deck.numCards()>0) 1 else 0)}
    }

    fun validatePlayerTurn(playerIdentity: PlayerIdentity): Unit =
            check(playerTurn == playerIdentity) {
                warnMsg("It's not the turn of the player $playerIdentity")
            }


    fun testInvariants() = invariantNumCardsInGame()

    // write
    protected fun UpdatePlayers(burracoPlayerInGame: PlayerInGame): List<PlayerInGame> = players.map { playerInGame ->
        if (playerInGame.identity() == burracoPlayerInGame.identity()) {
            burracoPlayerInGame
        } else
            playerInGame
    }

    //validation
    protected fun validatePlayerId(playerIdentity: PlayerIdentity): PlayerInGame =
            checkNotNull(players.find { p -> p.identity() == playerIdentity }) {
                errorMsg("Player $playerIdentity is not a player of this game ${identity()}")
            }

    private fun numCardsInGame(): Int {
        val playersCardsTot = players.map { player ->
            player.totalPlayerCards()
        }.fold(0) { total, item -> total + item }
        return playersCardsTot + burracoDeck.numCards() + playerDeck1.numCards() + playerDeck2.numCards() + discardPile.numCards()
    }

    private fun invariantNumCardsInGame(): Unit {
        assert(totalCardsRequired == numCardsInGame()) { "The cards in game are not ${totalCardsRequired}. Founds ${numCardsInGame()}" }
    }

    protected fun firstMazzettoAvailable(): PlayerDeck {
        check(showNumMazzettoAvailable() == 0){ warnMsg("Mazzetto list empty, all Mazzetto taken")}
        return listOf(playerDeck1, playerDeck2).first { playerDeck -> playerDeck.numCards() > 0 }
    }

//    protected fun mazzettoTaken(mazzettoDeck: PlayerDeck): MazzettoDecks {
//        check(list.find{m -> m == mazzettoDeck}!= null) {errorMsg("MazzettoDeck not found")}
//        return copy(list = list.minus(mazzettoDeck))
//    }


}