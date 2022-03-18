package com.abaddon83.burraco.game.models.game


import com.abaddon83.burraco.game.events.game.CardPickedFromDeck
import com.abaddon83.burraco.game.events.game.CardsPickedFromDiscardPile
import com.abaddon83.burraco.game.helpers.ValidationsTools
import com.abaddon83.burraco.game.models.decks.Deck
import com.abaddon83.burraco.game.models.decks.DiscardPile
import com.abaddon83.burraco.game.models.decks.PlayerDeck
import com.abaddon83.burraco.game.models.player.Player
import com.abaddon83.burraco.game.models.player.PlayerIdentity
import org.slf4j.Logger
import org.slf4j.LoggerFactory

data class GameExecutionPickUpPhase private constructor(
    override val id: GameIdentity,
    override val version: Long,
    override val players: List<Player>,
    val playerTurn: PlayerIdentity,
    val deck: Deck,
    val playerDeck1: PlayerDeck,
    val playerDeck2: PlayerDeck,
    val discardPile: DiscardPile
) : GameExecution(id,version,players, playerTurn, deck, playerDeck1, playerDeck2, discardPile) {
    override val log: Logger = LoggerFactory.getLogger(this::class.simpleName)

    companion object Factory {
        fun from(game: GameWaitingDealer): GameExecutionPickUpPhase = GameExecutionPickUpPhase(
                id = game.id,
                version = game.version,
                players = game.players,
                playerTurn = game.players.first().id,
                deck = Deck.create(game.deck),
                playerDeck1 = PlayerDeck.create(game.playerDeck1),
                playerDeck2 = PlayerDeck.create(game.playerDeck2),
                discardPile = DiscardPile.create(game.discardCard)
            )

//        fun create(
//            identity: GameIdentity,
//            players: List<PlayerInGame>,
//            burracoDeck: Deck,
//            playerDeck1: PlayerDeck,
//            playerDeck2: PlayerDeck,
//            discardPile: DiscardPile,
//            playerTurn: PlayerIdentity
//        ): GameExecutionInitialPhase {
//            val game = GameExecutionInitialPhase(
//                identity = identity,
//                players = players,
//                burracoDeck = burracoDeck,
//                playerDeck1 = playerDeck1,
//                playerDeck2 = playerDeck2,
//                discardPile = discardPile,
//                playerTurn = playerTurn
//            )
//            game.testInvariants()
//            return game
//        }

    }

    //When the turn start the player can pickUp a card from the Deck
    fun pickUpACardFromDeck(playerIdentity: PlayerIdentity): GameExecutionPlayPhase {
        checkNotNull(players.find {  player -> player.id == playerIdentity }){"Player $playerIdentity is not a player of the game $id"}
        checkNotNull(playerTurn == playerIdentity ){"It's not the turn of the player $playerIdentity"}
        check(deck.numCards()-1 >= 0 ){"Deck is empty"}
        return raiseEvent(CardPickedFromDeck.create(id, playerIdentity, deck.firstCard())) as GameExecutionPlayPhase
    }

    //When the turn start the player can pickUp all cards from the DiscardPile if it's not empty
    fun pickUpCardsFromDiscardPile(playerIdentity: PlayerIdentity): GameExecutionPlayPhase {
        checkNotNull(players.find {  player -> player.id == playerIdentity }){"Player $playerIdentity is not a player of the game $id"}
        checkNotNull(playerTurn == playerIdentity ){"It's not the turn of the player $playerIdentity"}
        return raiseEvent(CardsPickedFromDiscardPile.create(id, playerIdentity, discardPile.showCards())) as GameExecutionPlayPhase
    }

    private fun apply(event: CardPickedFromDeck): GameExecutionPlayPhase {
        check(event.aggregateId == id) { "Game Identity mismatch" }
        val updatedPlayers = ValidationsTools.updatePlayerInPlayers(players, event.playerIdentity) {
                player -> player.copy(cards = player.cards.plus(event.card))
        }
        val updatedDeck= deck.removeFirstCard(event.card)
        val updatedAggregate = copy(deck = updatedDeck, players = updatedPlayers)

        return GameExecutionPlayPhase.from(updatedAggregate)
    }

    private fun apply(event: CardsPickedFromDiscardPile): GameExecutionPlayPhase {
        check(event.aggregateId == id) { "Game Identity mismatch" }
        val updatedPlayers = ValidationsTools.updatePlayerInPlayers(players, event.playerIdentity) {
                player -> player.copy(cards = player.cards.plus(event.cards))
        }
        val updatedDiscardPile= discardPile.removeAllCards(event.cards)

        val updatedAggregate = copy(discardPile = updatedDiscardPile, players = updatedPlayers)

        return GameExecutionPlayPhase.from(updatedAggregate)
    }
}