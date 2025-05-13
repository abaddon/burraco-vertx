package com.abaddon83.burraco.game.models.game

import com.abaddon83.burraco.common.helpers.log
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.game.events.game.GameEnded
import com.abaddon83.burraco.game.events.game.NextPlayerTurnStarted
import com.abaddon83.burraco.game.helpers.playerCards
import com.abaddon83.burraco.game.helpers.playerTeam
import com.abaddon83.burraco.game.models.Team
import com.abaddon83.burraco.game.models.decks.Deck
import com.abaddon83.burraco.game.models.decks.DiscardPile
import com.abaddon83.burraco.game.models.decks.PlayerDeck
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.game.models.player.PlayerInGame


data class GameExecutionEndPhase private constructor(
    override val id: GameIdentity,
    override val version: Long,
    override val players: List<PlayerInGame>,
    val playerTurn: PlayerIdentity,
    val deck: Deck,
    val playerDeck1: PlayerDeck?,
    val playerDeck2: PlayerDeck?,
    val discardPile: DiscardPile,
    val teams: List<Team>
) : GameExecution(id, version, players, playerTurn, deck, playerDeck1, playerDeck2, discardPile, teams) {

    companion object Factory {
        fun from(game: GameExecutionPlayPhase): GameExecutionEndPhase = GameExecutionEndPhase(
            id = game.id,
            version = game.version,
            players = game.players,
            playerTurn = game.players.first().id,
            deck = game.deck,
            playerDeck1 = game.playerDeck1,
            playerDeck2 = game.playerDeck2,
            discardPile = game.discardPile,
            teams = game.teams
        )
    }

    fun startNextPlayerTurn(): GameExecutionPickUpPhase {
        check(!gameFinished()) { "The game is ended, the nex player can't start his/her turn" }
        return raiseEvent(NextPlayerTurnStarted.create(id, nextPlayerTurn())) as GameExecutionPickUpPhase
    }

    fun endGame(): GameTerminated {
        check(gameFinished()) { "The game is not ended" }
        return raiseEvent(GameEnded.create(id)) as GameTerminated
    }

    private fun apply(event: NextPlayerTurnStarted): GameExecutionPickUpPhase {
        check(event.aggregateId == id) { "Game Identity mismatch" }
        log.debug("The aggregate is applying the event ${event::class.simpleName} with id ${event.messageId}")
        val updatedAggregate = GameExecutionPickUpPhase.from(copy(playerTurn = event.playerIdentity))
        log.debug("It' the turn of player ${updatedAggregate.playerTurn.valueAsString()}")
        return updatedAggregate
    }

    private fun apply(event: GameEnded): GameTerminated {
        check(event.aggregateId == id) { "Game Identity mismatch" }
        log.debug("The aggregate is applying the event ${event::class.simpleName} with id ${event.messageId}")

        log.debug("Game ${id.valueAsString()} terminated")
        return GameTerminated.from(this)
    }

    private fun gameFinished(): Boolean = when (val team = teams.playerTeam(playerTurn)) {
        is Team -> (teamsHasAtLeastBurraco(team) && players.playerCards(playerTurn)!!.isEmpty())
        else -> false
    }

    private fun nextPlayerTurn(): PlayerIdentity {
        val playerIdentities = players.map { it.id }
        val currentIndex = playerIdentities.indexOf(playerTurn)
        val nextPlayer = (currentIndex + 1) % players.size
        return playerIdentities[nextPlayer]
    }
}
//data class GameExecutionDiscardPhase private constructor(
//    override val players: List<PlayerInGame>,
//    override val playerTurn: PlayerIdentity,
//    override val burracoDeck: Deck,
//    override val playerDeck1: PlayerDeck,
//    override val playerDeck2: PlayerDeck,
//    override val discardPile: DiscardPile,
//    override val identity: GameIdentity
//) : GameExecution(identity,"BurracoGameExecutionTurnEnd") {
//
//
//    override fun applyEvent(event: Event): Game {
//        log.info("apply event: ${event::class.simpleName.toString()}")
//        return when (event) {
//            is TurnEnded -> apply(event)
//            else -> throw UnsupportedEventException(event::class.java)
//        }
//    }
//
//
//    private fun apply(event: TurnEnded): BurracoGameExecutionTurnBeginning {
//        return BurracoGameExecutionTurnBeginning.create(
//                identity = identity(),
//                players = players,
//                burracoDeck = burracoDeck,
//                playerDeck1 = playerDeck1,
//                playerDeck2 = playerDeck2,
//                discardPile = discardPile,
//                playerTurn = event.nextPlayerTurn
//        )
//    }
//
//
//    fun pickupMazzetto(playerIdentity: PlayerIdentity): GameExecutionDiscardPhase {
//        val player = validatePlayerId(playerIdentity)
//        validatePlayerTurn(playerIdentity)
//        check(player.showMyCards().isEmpty()) { warnMsg("The player cannot pick up a Mazzetto if he still has cards") }
//        check(!player.isMazzettoTaken()) { warnMsg("The player cannot pick up a Mazzetto he already taken") }
//
//        //val mazzetto = mazzettoDecks.firstMazzettoAvailable()
//
//        return copy(
//       //         players = UpdatePlayers(player.pickUpMazzetto(mazzetto)),
//       //         mazzettoDecks = mazzettoDecks.mazzettoTaken(mazzetto)
//        )
//    }
//
//    fun nextPlayerTurn(playerIdentity: PlayerIdentity): BurracoGameExecutionTurnBeginning {
//        validatePlayerId(playerIdentity)
//        validatePlayerTurn(playerIdentity)
//        val list = players.map { it.identity() }
//        val nextPlayerTurn = list[(list.indexOf(playerTurn) + 1) % list.size]
//
//        return applyAndQueueEvent(
//                TurnEnded(identity = identity(), playerIdentity = playerIdentity, nextPlayerTurn = nextPlayerTurn)
//        )
//    }
//
//    fun completeGame(playerIdentity: PlayerIdentity): BurracoGameEnded {
//        val player = validatePlayerId(playerIdentity)
//        validatePlayerTurn(playerIdentity)
//        check(player.showMyCards().isEmpty()) { warnMsg("The player cannot complete the game with ${player.showMyCards().size} cards on hand") }
//        check(player.isMazzettoTaken()) { warnMsg("The player cannot complete the game if the small deck is not taken (status: ${player.isMazzettoTaken()})") }
//        check(player.burracoList().isNotEmpty()) { warnMsg("The player doesn't have a burraco") }
//        //TODO add the logic to check if the squad taken the pozzetto
//
//        return BurracoGameEnded.create(identity(), players, showNumMazzettoAvailable(), playerTurn)
//    }
//
//    override fun listOfPlayers(): List<PlayerInGame> = this.players
//
//    companion object Factory {
//        fun create(players: List<PlayerInGame>, playerTurn: PlayerIdentity, burracoDeck: Deck, playerDeck1: PlayerDeck, playerDeck2: PlayerDeck, discardPile: DiscardPile, identity: GameIdentity): GameExecutionDiscardPhase {
//            val game = GameExecutionDiscardPhase(
//                    players = players,
//                    playerTurn = playerTurn,
//                    burracoDeck = burracoDeck,
//                    playerDeck1 = playerDeck1,
//                    playerDeck2 = playerDeck2,
//                    discardPile = discardPile,
//                    identity = identity)
//            game.testInvariants()
//            return game
//        }
//    }
//}


//Events


