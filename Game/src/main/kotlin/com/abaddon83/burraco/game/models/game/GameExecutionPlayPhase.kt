package com.abaddon83.burraco.game.models.game

import com.abaddon83.burraco.common.helpers.log
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.game.events.game.*
import com.abaddon83.burraco.game.helpers.*
import com.abaddon83.burraco.game.helpers.StraightHelper.validStraight
import com.abaddon83.burraco.game.helpers.TrisHelper.validTris
import com.abaddon83.burraco.game.models.*
import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.decks.Deck
import com.abaddon83.burraco.game.models.decks.DiscardPile
import com.abaddon83.burraco.game.models.decks.PlayerDeck
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.StraightIdentity
import com.abaddon83.burraco.common.models.TrisIdentity
import com.abaddon83.burraco.game.models.player.PlayerInGame

data class GameExecutionPlayPhase private constructor(
    override val id: GameIdentity,
    override val version: Long,
    override val players: List<PlayerInGame>,
    val playerTurn: PlayerIdentity,
    val deck: Deck,
    val playerDeck1: PlayerDeck?,
    val playerDeck2: PlayerDeck?,
    val discardPile: DiscardPile,
    val teams: List<Team>
) : GameExecution(id, version, players, playerTurn, deck, playerDeck1, playerDeck2, discardPile,teams) {

    companion object Factory {
        fun from(game: GameExecutionPickUpPhase): GameExecutionPlayPhase = GameExecutionPlayPhase(
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

    fun dropTris(playerIdentity: PlayerIdentity, tris: Tris): GameExecutionPlayPhase {
        require(players.validPlayer(playerIdentity)) { "Player ${playerIdentity.valueAsString()} is not a player of the game ${id.valueAsString()}" }
        require(playerTurn == playerIdentity) { "It's not the turn of the player ${playerIdentity.valueAsString()}" }
        require(
            players.cardsBelongPlayer(
                playerIdentity,
                tris.cards
            )
        ) { "Tris's cards don't belong to player ${playerIdentity.valueAsString()}" }
        require(validRemainingCards(playerIdentity,tris.cards)) {"Not enough cards remaining on the player's hand"}

        return raiseEvent(TrisDropped.create(id, playerIdentity, tris)) as GameExecutionPlayPhase
    }

    fun appendCardsOnTris(
        playerIdentity: PlayerIdentity,
        cards: List<Card>,
        trisIdentity: TrisIdentity
    ): GameExecutionPlayPhase {
        require(players.validPlayer(playerIdentity)) { "Player ${playerIdentity.valueAsString()} is not a player of the game ${id.valueAsString()}" }
        require(playerTurn == playerIdentity) { "It's not the turn of the player ${playerIdentity.valueAsString()}" }
        require(
            players.cardsBelongPlayer(
                playerIdentity,
                cards
            )
        ) { "Cards to append to the tris ${trisIdentity.valueAsString()} don't belong to player ${playerIdentity.valueAsString()}" }
        require(
            players.trisBelongPlayer(
                playerIdentity,
                trisIdentity
            )
        ) { "Tris ${trisIdentity.valueAsString()} don't belong to player ${playerIdentity.valueAsString()}" }
        require(
            validTris(
                players.tris(playerIdentity, trisIdentity)?.cards
                    ?.plus(cards)
                    .orEmpty()
            )
        ) { "Cards can't be added to Tris ${trisIdentity.valueAsString()}" }
        require(validRemainingCards(playerIdentity,cards)) {"Not enough cards remaining on the player's hand"}

        return raiseEvent(CardsAddedToTris.create(id, playerIdentity, trisIdentity, cards)) as GameExecutionPlayPhase
    }


    fun dropStraight(playerIdentity: PlayerIdentity, straight: Straight): GameExecutionPlayPhase {
        require(players.validPlayer(playerIdentity)) { "Player ${playerIdentity.valueAsString()} is not a player of the game ${id.valueAsString()}" }
        require(playerTurn == playerIdentity) { "It's not the turn of the player ${playerIdentity.valueAsString()}" }
        require(
            players.cardsBelongPlayer(
                playerIdentity,
                straight.cards
            )
        ) { "Straight's cards don't belong to player ${playerIdentity.valueAsString()}" }
        require(validRemainingCards(playerIdentity,straight.cards)) {"Not enough cards remaining on the player's hand"}

        return raiseEvent(StraightDropped.create(id, playerIdentity, straight)) as GameExecutionPlayPhase
    }

    fun appendCardsOnStraight(
        playerIdentity: PlayerIdentity,
        cards: List<Card>,
        straightIdentity: StraightIdentity
    ): GameExecutionPlayPhase {
        require(players.validPlayer(playerIdentity)) { "Player ${playerIdentity.valueAsString()} is not a player of the game ${id.valueAsString()}" }
        require(playerTurn == playerIdentity) { "It's not the turn of the player ${playerIdentity.valueAsString()}" }
        require(
            players.cardsBelongPlayer(
                playerIdentity,
                cards
            )
        ) { "Cards to append to the straight ${straightIdentity.valueAsString()} don't belong to player ${playerIdentity.valueAsString()}" }
        require(
            players.straightBelongPlayer(
                playerIdentity,
                straightIdentity
            )
        ) { "Straight ${straightIdentity.valueAsString()} don't belong to player ${playerIdentity.valueAsString()}" }
        require(
            validStraight(
                players.straight(playerIdentity, straightIdentity)?.cards
                    ?.plus(cards)
                    .orEmpty()
            )
        ) { "Cards can't be added to straight ${straightIdentity.valueAsString()}" }
        require(validRemainingCards(playerIdentity,cards)) {"Not enough cards remaining on the player's hand"}

        return raiseEvent(CardsAddedToStraight.create(id, playerIdentity, straightIdentity, cards)) as GameExecutionPlayPhase
    }

    fun pickupPlayerDeck(playerIdentity: PlayerIdentity): GameExecutionPlayPhase {
        require(players.validPlayer(playerIdentity)) { "Player ${playerIdentity.valueAsString()} is not a player of the game ${id.valueAsString()}" }
        require(playerTurn == playerIdentity) { "It's not the turn of the player ${playerIdentity.valueAsString()}" }
        require(players.playerCards(playerIdentity).isNullOrEmpty()){"Player ${playerIdentity.valueAsString()} still has cards in their hand" }
        require(playerDeckAvailable().isNotEmpty()){"No more player deck available"}
        require(!(teams.playerTeam(playerIdentity)?.playerDeckPickedUp ?: false)){"Player ${playerIdentity.valueAsString()}'s team has already pickedUp its playerDeck"}

        return raiseEvent(CardsPickedFromPlayerDeckDuringTurn.create(id, playerIdentity, playerDeckAvailable())) as GameExecutionPlayPhase
    }

    fun dropCardOnDiscardPile(playerIdentity: PlayerIdentity, card: Card): GameExecutionEndPhase {
        require(players.validPlayer(playerIdentity)) { "Player ${playerIdentity.valueAsString()} is not a player of the game ${id.valueAsString()}" }
        require(playerTurn == playerIdentity) { "It's not the turn of the player ${playerIdentity.valueAsString()}" }
        require(
            players.cardsBelongPlayer(
                playerIdentity,
                listOf(card)
            )
        ) { "Cards to discard don't belong to player ${playerIdentity.valueAsString()}" }

        return raiseEvent(CardsDiscarded.create(id, playerIdentity, card)) as GameExecutionEndPhase
    }


    private fun apply(event: TrisDropped): GameExecutionPlayPhase {
        check(event.aggregateId == id) { "Game Identity mismatch" }
        log.debug("The aggregate is applying the event ${event::class.simpleName} with id ${event.messageId}")
        val updatedPlayers = players.updatePlayer(event.playerIdentity) { player ->
            player.copy(
                cards = player.cards.minus(event.cards),
                listOfTris = player.listOfTris.plus(Tris.create(event.trisIdentity,event.cards))
            )
        }
        log.debug("Tris has ${updatedPlayers.tris(event.playerIdentity,event.trisIdentity)?.cards?.size} cards and Player ${event.playerIdentity.valueAsString()} has ${updatedPlayers.playerCards(event.playerIdentity)?.size} cards")
        return copy(players = updatedPlayers)
    }

    private fun apply(event: CardsAddedToTris): GameExecutionPlayPhase {
        check(event.aggregateId == id) { "Game Identity mismatch" }
        log.debug("The aggregate is applying the event ${event::class.simpleName} with id ${event.messageId}")
        val updatedPlayers = players.updatePlayer(event.playerIdentity) { player ->
            player.copy(
                cards = player.cards.minus(event.cards),
                listOfTris = player.listOfTris.updateTris(event.trisIdentity) { tris ->
                    tris.copy(cards = tris.cards.plus(event.cards))
                }
            )
        }
        log.debug("Tris has ${updatedPlayers.tris(event.playerIdentity,event.trisIdentity)?.cards?.size} cards and Player ${event.playerIdentity.valueAsString()} has ${updatedPlayers.playerCards(event.playerIdentity)?.size} cards")
        return copy(players = updatedPlayers)
    }

    private fun apply(event: StraightDropped): GameExecutionPlayPhase {
        check(event.aggregateId == id) { "Game Identity mismatch" }
        log.debug("The aggregate is applying the event ${event::class.simpleName} with id ${event.messageId}")
        val updatedPlayers = players.updatePlayer(event.playerIdentity) { player ->
            player.copy(
                cards = player.cards.minus(event.cards),
                listOfStraight = player.listOfStraight.plus(Straight.create(event.straightIdentity,event.cards))
            )
        }
        log.debug("Straight has ${updatedPlayers.straight(event.playerIdentity,event.straightIdentity)?.cards?.size} cards and Player ${event.playerIdentity.valueAsString()} has ${updatedPlayers.playerCards(event.playerIdentity)?.size} cards")
        return copy(players = updatedPlayers)
    }

    private fun apply(event: CardsAddedToStraight): GameExecutionPlayPhase {
        check(event.aggregateId == id) { "Game Identity mismatch" }
        log.debug("The aggregate is applying the event ${event::class.simpleName} with id ${event.messageId}")
        val updatedPlayers = players.updatePlayer(event.playerIdentity) { player ->
            player.copy(
                cards = player.cards.minus(event.cards),
                listOfStraight = player.listOfStraight.updateStraight(event.straightIdentity) { straight ->
                    straight.copy(cards = straight.cards.plus(event.cards))
                }
            )
        }

        log.debug("Straight has ${updatedPlayers.straight(event.playerIdentity,event.straightIdentity)?.cards?.size} cards and Player ${event.playerIdentity.valueAsString()} has ${updatedPlayers.playerCards(event.playerIdentity)?.size} cards")
        return copy(players = updatedPlayers)
    }


    private fun apply(event: CardsPickedFromPlayerDeckDuringTurn): GameExecutionPlayPhase {
        check(event.aggregateId == id) { "Game Identity mismatch" }
        log.debug("The aggregate is applying the event ${event::class.simpleName} with id ${event.messageId}")

        val updatedPlayers = players.updatePlayer(event.playerIdentity) { player: PlayerInGame ->
            player.copy(cards = player.cards.plus(event.cards))
        }

        //TODO to review, there is an hidden event  :(
        val updatedTeams: List<Team> = when(teams.playerTeam(event.playerIdentity)){
            is Team -> teams.updateTeam(event.playerIdentity){ team ->
                team.copy(playerDeckPickedUp = true)
            }
            else -> {
                if(players.size == 3)
                    TeamsHelper.buildTeamsWith3Players(event.playerIdentity,players.map { it.id })
                else {
                    assert(false) { "Something bad happened" }
                    teams
                }
            }
        }
        val updatedAggregate= when(playerDeck1){
            is PlayerDeck -> copy(playerDeck1 = null, players = updatedPlayers, teams = updatedTeams)

            else -> {
                when(playerDeck2){
                    is PlayerDeck -> copy(playerDeck2 = null, players = updatedPlayers, teams = updatedTeams)
                    else -> {
                        log.error("Event ${event.messageId} is wrong! The event is ignored ")
                        this
                    }
                }
            }
        }
        log.debug("PlayerDeck1 has ${updatedAggregate.playerDeck1?.numCards()?:0} cards, PlayerDeck2 has ${updatedAggregate.playerDeck2?.numCards()?:0} cards and Player ${event.playerIdentity.valueAsString()} has ${updatedPlayers.playerCards(event.playerIdentity)?.size} cards")
        return updatedAggregate
    }

    private fun apply(event: CardsDiscarded): GameExecutionEndPhase {
        check(event.aggregateId == id) { "Game Identity mismatch" }
        log.debug("The aggregate is applying the event ${event::class.simpleName} with id ${event.messageId}")
        val updatedPlayers = players.updatePlayer(event.playerIdentity) { player ->
            player.copy(cards = player.cards.minus(event.card))
        }

        val updatedAggregate = GameExecutionEndPhase.from(copy(players = updatedPlayers, discardPile = discardPile.addCard(event.card)))
        log.debug("DiscardPile has ${updatedAggregate.discardPile.numCards()} cards and Player ${event.playerIdentity.valueAsString()} has ${updatedPlayers.playerCards(event.playerIdentity)?.size} cards")
        return updatedAggregate
    }



    private fun playerDeckAvailable(): List<Card>{
        return when(playerDeck1){
            is PlayerDeck -> playerDeck1.cards
            else -> {
                when(playerDeck2){
                    is PlayerDeck -> playerDeck2.cards
                    else -> listOf()
                }
            }
        }
    }

    private fun validRemainingCards(playerIdentity: PlayerIdentity, cardsToDrop: List<Card>): Boolean {

        val minCardsAcceptable=when(val team = teams.playerTeam(playerIdentity)){
            is Team ->{
                if (!team.playerDeckPickedUp) /*Team need to pickup a discardPile*/
                    0
                else{ /*Team pickedup the discardPile*/
                    if(teamsHasAtLeastBurraco(team)) /*Team has a burraco*/
                        1
                    else /*Team doesn't have a burraco*/
                        2
                }
            }
            else -> {
                /*Team need to pickup a discardPile*/
                0
            }
        }
        return players.playerCards(playerIdentity)!!.minus(cardsToDrop).size >= minCardsAcceptable
    }


}