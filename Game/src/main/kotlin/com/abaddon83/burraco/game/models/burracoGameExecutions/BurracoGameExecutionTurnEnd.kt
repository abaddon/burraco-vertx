package com.abaddon83.burraco.game.models.burracoGameExecutions

import com.abaddon83.utils.ddd.Event
import com.abaddon83.burraco.common.events.TurnEnded
import com.abaddon83.burraco.game.models.burracoGameExecutions.playerInGames.PlayerInGame
import com.abaddon83.burraco.game.models.burracoGameendeds.BurracoGameEnded
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.utils.ddd.writeModel.UnsupportedEventException
import com.abaddon83.burraco.game.models.*
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameIdentity

data class BurracoGameExecutionTurnEnd private constructor(
    override val players: List<PlayerInGame>,
    override val playerTurn: PlayerIdentity,
    override val burracoDeck: BurracoDeck,
    override val playerDeck1: PlayerDeck,
    override val playerDeck2: PlayerDeck,
    override val discardPile: DiscardPile,
    override val identity: GameIdentity
) : BurracoGameExecution(identity,"BurracoGameExecutionTurnEnd") {


    override fun applyEvent(event: Event): Game {
        log.info("apply event: ${event::class.simpleName.toString()}")
        return when (event) {
            is TurnEnded -> apply(event)
            else -> throw UnsupportedEventException(event::class.java)
        }
    }


    private fun apply(event: TurnEnded): BurracoGameExecutionTurnBeginning {
        return BurracoGameExecutionTurnBeginning.create(
                identity = identity(),
                players = players,
                burracoDeck = burracoDeck,
                playerDeck1 = playerDeck1,
                playerDeck2 = playerDeck2,
                discardPile = discardPile,
                playerTurn = event.nextPlayerTurn
        )
    }


    fun pickupMazzetto(playerIdentity: PlayerIdentity): BurracoGameExecutionTurnEnd {
        val player = validatePlayerId(playerIdentity)
        validatePlayerTurn(playerIdentity)
        check(player.showMyCards().isEmpty()) { warnMsg("The player cannot pick up a Mazzetto if he still has cards") }
        check(!player.isMazzettoTaken()) { warnMsg("The player cannot pick up a Mazzetto he already taken") }

        //val mazzetto = mazzettoDecks.firstMazzettoAvailable()

        return copy(
       //         players = UpdatePlayers(player.pickUpMazzetto(mazzetto)),
       //         mazzettoDecks = mazzettoDecks.mazzettoTaken(mazzetto)
        )
    }

    fun nextPlayerTurn(playerIdentity: PlayerIdentity): BurracoGameExecutionTurnBeginning {
        validatePlayerId(playerIdentity)
        validatePlayerTurn(playerIdentity)
        val list = players.map { it.identity() }
        val nextPlayerTurn = list[(list.indexOf(playerTurn) + 1) % list.size]

        return applyAndQueueEvent(
                TurnEnded(identity = identity(), playerIdentity = playerIdentity, nextPlayerTurn = nextPlayerTurn)
        )
    }

    fun completeGame(playerIdentity: PlayerIdentity): BurracoGameEnded {
        val player = validatePlayerId(playerIdentity)
        validatePlayerTurn(playerIdentity)
        check(player.showMyCards().isEmpty()) { warnMsg("The player cannot complete the game with ${player.showMyCards().size} cards on hand") }
        check(player.isMazzettoTaken()) { warnMsg("The player cannot complete the game if the small deck is not taken (status: ${player.isMazzettoTaken()})") }
        check(player.burracoList().isNotEmpty()) { warnMsg("The player doesn't have a burraco") }
        //TODO add the logic to check if the squad taken the pozzetto

        return BurracoGameEnded.create(identity(), players, showNumMazzettoAvailable(), playerTurn)
    }

    override fun listOfPlayers(): List<PlayerInGame> = this.players

    companion object Factory {
        fun create(players: List<PlayerInGame>, playerTurn: PlayerIdentity, burracoDeck: BurracoDeck, playerDeck1: PlayerDeck, playerDeck2: PlayerDeck, discardPile: DiscardPile, identity: GameIdentity): BurracoGameExecutionTurnEnd {
            val game = BurracoGameExecutionTurnEnd(
                    players = players,
                    playerTurn = playerTurn,
                    burracoDeck = burracoDeck,
                    playerDeck1 = playerDeck1,
                    playerDeck2 = playerDeck2,
                    discardPile = discardPile,
                    identity = identity)
            game.testInvariants()
            return game
        }
    }
}


//Events


