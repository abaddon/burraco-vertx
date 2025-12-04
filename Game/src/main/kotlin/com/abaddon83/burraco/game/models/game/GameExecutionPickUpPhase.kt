package com.abaddon83.burraco.game.models.game


import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.event.game.CardPickedFromDeck
import com.abaddon83.burraco.common.models.event.game.CardsPickedFromDiscardPile
import com.abaddon83.burraco.game.helpers.playerCards
import com.abaddon83.burraco.game.helpers.updatePlayer
import com.abaddon83.burraco.game.helpers.validPlayer
import com.abaddon83.burraco.game.models.Team
import com.abaddon83.burraco.game.models.decks.Deck
import com.abaddon83.burraco.game.models.decks.DiscardPile
import com.abaddon83.burraco.game.models.decks.PlayerDeck
import com.abaddon83.burraco.game.models.player.PlayerInGame
import io.github.abaddon.kcqrs.core.helpers.KcqrsLoggerFactory.log

data class GameExecutionPickUpPhase(
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

        fun from(game: GameExecutionEndPhase): GameExecutionPickUpPhase {
            return GameExecutionPickUpPhase(
                id = game.id,
                version = game.version,
                players = game.players,
                playerTurn = game.playerTurn,
                deck = game.deck,
                playerDeck1 = game.playerDeck1,
                playerDeck2 = game.playerDeck2,
                discardPile = game.discardPile,
                teams = game.teams
            )
        }

        fun from(
            game: GameWaitingDealer,
            playerTurn: PlayerIdentity,
            teams: List<List<PlayerIdentity>>
        ): GameExecutionPickUpPhase {
            return GameExecutionPickUpPhase(
                id = game.id,
                version = game.version,
                players = game.players.map { PlayerInGame.from(it) },
                playerTurn = playerTurn,
                deck = Deck.create(game.deck),
                playerDeck1 = PlayerDeck.create(game.playerDeck1),
                playerDeck2 = PlayerDeck.create(game.playerDeck2),
                discardPile = DiscardPile.create(game.discardDeck),
                teams = createTeams(teams)
            )
        }

        private fun createTeams(teams: List<List<PlayerIdentity>>): List<Team> =
            teams.map { team -> Team(team) }
    }


    //When the turn start the player can pickUp a card from the Deck
    fun pickUpACardFromDeck(playerIdentity: PlayerIdentity): GameExecutionPlayPhase {
        check(players.validPlayer(playerIdentity)) { "Player ${playerIdentity.valueAsString()} is not a player of the game ${id.valueAsString()}" }
        check(playerTurn == playerIdentity) { "It's not the turn of the player ${playerIdentity.valueAsString()}" }
        check(deck.numCards() - 1 >= 0) { "Deck is empty" }
        return raiseEvent(CardPickedFromDeck.create(id, playerIdentity, deck.firstCard())) as GameExecutionPlayPhase
    }

    //When the turn start the player can pickUp all cards from the DiscardPile if it's not empty
    fun pickUpCardsFromDiscardPile(playerIdentity: PlayerIdentity): GameExecutionPlayPhase {
        check(players.validPlayer(playerIdentity)) { "Player ${playerIdentity.valueAsString()} is not a player of the game ${id.valueAsString()}" }
        check(playerTurn == playerIdentity) { "It's not the turn of the player ${playerIdentity.valueAsString()}" }
        return raiseEvent(
            CardsPickedFromDiscardPile.create(
                id,
                playerIdentity,
                discardPile.showCards()
            )
        ) as GameExecutionPlayPhase
    }

    private fun apply(event: CardPickedFromDeck): GameExecutionPlayPhase {
        check(event.aggregateId == id) { "Game Identity mismatch" }
        log.debug("The aggregate is applying the event ${event::class.simpleName} with id ${event.messageId}")
        val updatedPlayers = players.updatePlayer(event.playerIdentity) { player: PlayerInGame ->
            player.copy(cards = player.cards.plus(event.card))
        }
        val updatedDeck = deck.removeFirstCard(event.card)
        val updatedAggregate = copy(deck = updatedDeck, players = updatedPlayers)
        log.debug(
            "Desk has ${updatedDeck.numCards()} cards and Player ${event.playerIdentity.valueAsString()} has ${
                updatedPlayers.playerCards(
                    event.playerIdentity
                )?.size
            } cards"
        )
        return GameExecutionPlayPhase.from(updatedAggregate)
    }

    private fun apply(event: CardsPickedFromDiscardPile): GameExecutionPlayPhase {
        check(event.aggregateId == id) { "Game Identity mismatch" }
        log.debug("The aggregate is applying the event ${event::class.simpleName} with id ${event.messageId}")
        val updatedPlayers = players.updatePlayer(event.playerIdentity) { player: PlayerInGame ->
            player.copy(cards = player.cards.plus(event.cards))
        }
        val updatedDiscardPile = discardPile.removeAllCards(event.cards)

        val updatedAggregate = copy(discardPile = updatedDiscardPile, players = updatedPlayers)
        log.debug(
            "Discard Pile has ${updatedDiscardPile.numCards()} cards and Player ${event.playerIdentity.valueAsString()} has ${
                updatedPlayers.playerCards(
                    event.playerIdentity
                )?.size
            } cards"
        )
        return GameExecutionPlayPhase.from(updatedAggregate)
    }
}