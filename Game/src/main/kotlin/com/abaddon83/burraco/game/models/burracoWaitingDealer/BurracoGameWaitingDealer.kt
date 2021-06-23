package com.abaddon83.burraco.game.models.burracoWaitingDealer

import com.abaddon83.burraco.common.events.*
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.utils.ddd.Event
import com.abaddon83.utils.ddd.writeModel.UnsupportedEventException
import com.abaddon83.burraco.game.models.*
import com.abaddon83.burraco.game.models.burracoGameExecutions.BurracoGameExecutionTurnBeginning
import com.abaddon83.burraco.game.models.burracoGameExecutions.playerInGames.PlayerInGame
import io.vertx.kotlin.ext.consul.checkListOf

data class BurracoGameWaitingDealer private constructor(
    override val identity: GameIdentity,
    override val players: List<BurracoPlayer>,
    override val deck: BurracoDeck,
    val playerDeck1: PlayerDeck,
    val playerDeck2: PlayerDeck,
    val discardCard: Card?
) : BurracoGame(identity, "BurracoGameExecutionTurnBeginning") {

    companion object Factory {
        fun create(identity: GameIdentity, players: List<BurracoPlayer>): BurracoGameWaitingDealer {
            return BurracoGameWaitingDealer(
                identity = identity,
                players = players,
                deck = BurracoDeck.create(listOf()),
                playerDeck1 = PlayerDeck(mutableListOf<Card>()),
                playerDeck2 = PlayerDeck(mutableListOf<Card>()),
                discardCard = null
            )
        }
    }

    fun startGame(): BurracoGameExecutionTurnBeginning {
        check(areCardsDealt()){"the dealer has not finished dealing the cards"}
        return applyAndQueueEvent(GameStarted(identity))
    }

    fun dealPlayerCard(playerIdentity: PlayerIdentity, card: Card): BurracoGameWaitingDealer{
        val player = players.find { it.identity() == playerIdentity }
        checkNotNull(player){"The playerIdentity not found"}
        check(player.cards.size < numInitialPlayerCards){"The player has already enough card"}
        return applyAndQueueEvent(CardAssignedToPlayer(identity,playerIdentity,card))
    }

    fun dealPlayerDeckCard(playerDeckId: Int, card: Card): BurracoGameWaitingDealer{
        check(playerDeckId in (0..1)){"Player Deck id not valid"}
        val deckCards = when(playerDeckId){
            0 -> playerDeck1.numCards()
            1 -> playerDeck2.numCards()
            else -> 100
        }
        check(deckCards < sizePlayerDeck(playerDeckId)){"The player deck has already enough card"}
        return applyAndQueueEvent(CardAssignedToPlayerDeck(identity, playerDeckId, card))
    }

    fun dealDiscardDeckCard(card: Card): BurracoGameWaitingDealer{
        check(discardCard == null){"Discard card already defined"}
        return applyAndQueueEvent(CardAssignedToDiscardDeck(identity, card))
    }

    fun dealDeckCard(card: Card): BurracoGameWaitingDealer{
        check(deck.numCards() < initialSizeDeck()){"The Deck has already enough card"}
        return applyAndQueueEvent(CardAssignedToDeck(identity, card))
    }

    override fun applyEvent(event: Event): BurracoGame {
        log.info("apply event: ${event::class.simpleName.toString()}")
        return when (event) {
            is CardAssignedToPlayer -> apply(event)
            is CardAssignedToPlayerDeck -> apply(event)
            is CardAssignedToDeck -> apply(event)
            is CardAssignedToDiscardDeck -> apply(event)
            is GameStarted -> apply(event)
            else -> throw UnsupportedEventException(event::class.java)
        }
    }

    private fun apply(event: CardAssignedToPlayer): BurracoGameWaitingDealer {
        check(event.identity == identity) { "Game Identity mismatch" }
        val updatedPlayers = players.map { player ->
            when (player.identity()) {
                event.player -> player.copy(cards = player.cards.plus(event.card))
                else -> player
            }
        }
        check(updatedPlayers != players)
        return copy(players = updatedPlayers)
    }

    private fun apply(event: CardAssignedToPlayerDeck): BurracoGameWaitingDealer {
        check(event.identity == identity) { "Game Identity mismatch" }
        check(event.playerDeckId.toInt() in 0..1) { "Game Identity mismatch" }
        return when (event.playerDeckId) {
            0 -> {
                val updatedPlayerDeck = playerDeck1.copy(cards = playerDeck1.cards.plus(event.card).toMutableList())
                copy(playerDeck1 = updatedPlayerDeck)
            }
            1 -> {
                val updatedPlayerDeck = playerDeck2.copy(cards = playerDeck2.cards.plus(event.card).toMutableList())
                copy(playerDeck2 = updatedPlayerDeck)
            }
            else -> {
                log.warn("event discarded, playerDesk invalid")
                this
            }
        }
    }

    private fun apply(event: CardAssignedToDeck): BurracoGameWaitingDealer {
        check(event.identity == identity) { "Game Identity mismatch" }
        val updatedDeck = deck.copy(cards = deck.cards.plus(event.card).toMutableList())
        return copy(deck = updatedDeck)
    }

    private fun apply(event: CardAssignedToDiscardDeck): BurracoGameWaitingDealer {
        check(event.identity == identity) { "Game Identity mismatch" }
        return copy(discardCard = event.card)
    }

    private fun apply(event: GameStarted): BurracoGameExecutionTurnBeginning {
        check(event.identity == identity) { "Game Identity mismatch" }
        check(discardCard != null) { "Game Identity mismatch" }
        return  BurracoGameExecutionTurnBeginning.create(
            identity = identity,
            players = players.map { player -> PlayerInGame.create(playerIdentity = player.identity(),cards = player.cards)  },
            burracoDeck = deck,
            playerDeck1 = playerDeck1,
            playerDeck2 = playerDeck2,
            discardPile = DiscardPile.create(listOf(discardCard)),
            playerTurn = players.first().identity()
        )
    }

    private fun areCardsDealt(): Boolean{
        val numPlayerDeck1Cards = playerDeck1.numCards()
        val numPlayerDeck2Cards = playerDeck2.numCards()
        val numDeckCards = deck.numCards()
        val numPlayersCards = players.fold(0){ sum, player -> sum + player.cards.size}
        val numDiscardCard = if (discardCard == null) 0 else 1
        val assignedCards = numDeckCards+
                numPlayerDeck1Cards+
                numPlayerDeck2Cards+
                numPlayersCards+
                numDiscardCard;

        val result = assignedCards == totalCardsRequired
        if(!result){
            log.warn("Total cards required are $totalCardsRequired but there are only $assignedCards cards \n"+
                    "players: ${players.size} with $numPlayersCards cards \n"+
                    "playerDeck1 with $numPlayerDeck1Cards cards \n"+
                    "playerDeck2 with $numPlayerDeck2Cards cards \n"+
                    "discardDeck with $numDiscardCard cards \n"+
                    "deck with $numDeckCards cards \n")
        }
        return result
    }

//    //When the turn start the player can pickUp a card from the Deck
//    fun pickUpACardFromDeck(playerIdentity: PlayerIdentity): BurracoGameExecutionTurnExecution {
//        val player = validatePlayerId(playerIdentity)
//        validatePlayerTurn(playerIdentity)
//        return applyAndQueueEvent(CardPickedFromDeck(identity, player.identity(), burracoDeck.getFirstCard()))
//    }
//
//    //When the turn start the player can pickUp all cards from the DiscardPile if it's not empty
//    fun pickUpCardsFromDiscardPile(playerIdentity: PlayerIdentity): BurracoGameExecutionTurnExecution {
//        val player = validatePlayerId(playerIdentity)
//        validatePlayerTurn(playerIdentity)
//        return applyAndQueueEvent(CardsPickedFromDiscardPile(identity, player.identity(), discardPile.grabAllCards()))
//    }
//
//    override fun applyEvent(event: Event): BurracoGame {
//        log.info("apply event: ${event::class.simpleName.toString()}")
//        return when (event) {
//            is CardPickedFromDeck -> apply(event)
//            is CardsPickedFromDiscardPile -> apply(event)
//            else -> throw UnsupportedEventException(event::class.java)
//        }
//    }
//
//    private fun apply(event: CardPickedFromDeck): BurracoGameExecutionTurnExecution {
//        val player = players.find { p -> p.identity() == event.playerIdentity }!!
//
//        return BurracoGameExecutionTurnExecution.create(
//                identity = this.identity(),
//                players = UpdatePlayers(player.addCardsOnMyCard(listOf(event.card))),
//                burracoDeck = BurracoDeck.create(this.burracoDeck.cards.minusElement(event.card)),
//                mazzettoDecks = this.mazzettoDecks,
//                discardPile = this.discardPile,
//                playerTurn = this.playerTurn)
//    }
//
//    private fun apply(event: CardsPickedFromDiscardPile): BurracoGameExecutionTurnExecution {
//        val player = players.find { p -> p.identity() == event.playerIdentity }!!
//
//        return BurracoGameExecutionTurnExecution.create(
//                identity = this.identity(),
//                players = UpdatePlayers(player.addCardsOnMyCard(event.cards)),
//                burracoDeck = this.burracoDeck,
//                mazzettoDecks = this.mazzettoDecks,
//                discardPile = DiscardPile.create(discardPile.showCards().minus(event.cards)),
//                playerTurn = this.playerTurn
//        )
//    }
}