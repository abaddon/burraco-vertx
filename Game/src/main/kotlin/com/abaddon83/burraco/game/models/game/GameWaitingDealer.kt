package com.abaddon83.burraco.game.models.game

import com.abaddon83.burraco.game.events.game.CardDealtWithPlayer
import com.abaddon83.burraco.game.helpers.GameConfig
import com.abaddon83.burraco.game.helpers.ValidationsTools.playerCards
import com.abaddon83.burraco.game.helpers.ValidationsTools.playerIsContained
import com.abaddon83.burraco.game.helpers.ValidationsTools.updatePlayerInPlayers
import com.abaddon83.burraco.game.models.BurracoDeck
import com.abaddon83.burraco.game.models.DiscardPile
import com.abaddon83.burraco.game.models.PlayerDeck
import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.player.Player
import com.abaddon83.burraco.game.models.player.PlayerIdentity
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

data class GameWaitingDealer private constructor(
    override val id: GameIdentity,
    override val version: Long,
    override val players: List<Player>,
    val deck: BurracoDeck,
    val playerDeck1: PlayerDeck,
    val playerDeck2: PlayerDeck,
    val discardCard: DiscardPile
) : Game() {
    override val log: Logger = LoggerFactory.getLogger(this::class.simpleName)
    override val uncommittedEvents: MutableCollection<IDomainEvent> = mutableListOf()


    companion object Factory {
        fun from(gameDraft: GameDraft): GameWaitingDealer =
            GameWaitingDealer(
                gameDraft.id,
                gameDraft.version,
                gameDraft.players,
                deck = BurracoDeck.create(listOf()),
                PlayerDeck(mutableListOf<Card>()),
                PlayerDeck(mutableListOf<Card>()),
                DiscardPile.create(listOf())
            )
//        fun create(identity: GameIdentity, players: List<Player>): BurracoGameWaitingDealer {
//            return BurracoGameWaitingDealer(
//                identity = identity,
//                players = players,
//                deck = BurracoDeck.create(listOf()),
//                playerDeck1 = PlayerDeck(mutableListOf<Card>()),
//                playerDeck2 = PlayerDeck(mutableListOf<Card>()),
//                discardCard = null
//            )
//        }
    }

//    fun startGame(): BurracoGameExecutionTurnBeginning {
//        check(areCardsDealt()){"the dealer has not finished dealing the cards"}
//        return applyAndQueueEvent(GameStarted(identity))
//    }

    fun dealPlayerCard(playerIdentity: PlayerIdentity, card: Card): GameWaitingDealer {
        require(
            playerIsContained(
                playerIdentity,
                players
            )
        ) { "Player ${playerIdentity.valueAsString()} is not part of the game ${id.valueAsString()}" }
        val numPlayerCards = playerCards(playerIdentity, players)!!.size + 1
        check(GameConfig.NUM_PLAYER_CARDS >= numPlayerCards) { "Player ${playerIdentity.valueAsString()} has too many cards ($numPlayerCards)" }
        return raiseEvent(CardDealtWithPlayer.create(id, playerIdentity, card)) as GameWaitingDealer
    }

//    fun dealPlayerDeckCard(playerDeckId: Int, card: Card): GameWaitingDealer {
//        check(playerDeckId in (0..1)) { "Player Deck id not valid" }
//        val deckCards = when (playerDeckId) {
//            0 -> playerDeck1.numCards()
//            1 -> playerDeck2.numCards()
//            else -> 100
//        }
//        check(deckCards < sizePlayerDeck(playerDeckId)) { "The player deck has already enough card" }
//        return applyAndQueueEvent(CardAssignedToPlayerDeck(identity, playerDeckId, card))
//    }
//
//    fun dealDiscardDeckCard(card: Card): GameWaitingDealer {
//        check(discardCard == null) { "Discard card already defined" }
//        return applyAndQueueEvent(CardAssignedToDiscardDeck(identity, card))
//    }
//
//    fun dealDeckCard(card: Card): GameWaitingDealer {
//        check(deck.numCards() < initialSizeDeck()) { "The Deck has already enough card" }
//        return applyAndQueueEvent(CardAssignedToDeck(identity, card))
//    }

//    override fun applyEvent(event: Event): Game {
//        log.info("apply event: ${event::class.simpleName.toString()}")
//        return when (event) {
//            is CardAssignedToPlayer -> apply(event)
//            is CardAssignedToPlayerDeck -> apply(event)
//            is CardAssignedToDeck -> apply(event)
//            is CardAssignedToDiscardDeck -> apply(event)
//            is GameStarted -> apply(event)
//            else -> throw UnsupportedEventException(event::class.java)
//        }
//    }

    private fun apply(event: CardDealtWithPlayer): GameWaitingDealer {
        check(event.aggregateId == id) { "Game Identity mismatch" }
        val updatedPlayers = updatePlayerInPlayers(
            players,
            event.playerId
        ) { player -> player.copy(cards = player.cards.plus(event.card)) }
        //check(updatedPlayers.sumOf { it.cards.size } == players.sumOf { it.cards.size } + 1) { "Player ${event.playerId} doesn't have the right number of cards" }
        return copy(players = updatedPlayers)
    }

//    private fun apply(event: CardAssignedToPlayerDeck): GameWaitingDealer {
//        check(event.identity == identity) { "Game Identity mismatch" }
//        check(event.playerDeckId.toInt() in 0..1) { "Game Identity mismatch" }
//        return when (event.playerDeckId) {
//            0 -> {
//                val updatedPlayerDeck = playerDeck1.copy(cards = playerDeck1.cards.plus(event.card).toMutableList())
//                copy(playerDeck1 = updatedPlayerDeck)
//            }
//            1 -> {
//                val updatedPlayerDeck = playerDeck2.copy(cards = playerDeck2.cards.plus(event.card).toMutableList())
//                copy(playerDeck2 = updatedPlayerDeck)
//            }
//            else -> {
//                log.warn("event discarded, playerDesk invalid")
//                this
//            }
//        }
//    }
//
//    private fun apply(event: CardAssignedToDeck): GameWaitingDealer {
//        check(event.identity == identity) { "Game Identity mismatch" }
//        val updatedDeck = deck.copy(cards = deck.cards.plus(event.card).toMutableList())
//        return copy(deck = updatedDeck)
//    }
//
//    private fun apply(event: CardAssignedToDiscardDeck): GameWaitingDealer {
//        check(event.identity == identity) { "Game Identity mismatch" }
//        return copy(discardCard = event.card)
//    }
//
//    private fun apply(event: GameStarted): BurracoGameExecutionTurnBeginning {
//        check(event.identity == identity) { "Game Identity mismatch" }
//        check(discardCard != null) { "Game Identity mismatch" }
//        return BurracoGameExecutionTurnBeginning.create(
//            identity = identity,
//            players = players.map { player ->
//                PlayerInGame.create(
//                    playerIdentity = player.identity(),
//                    cards = player.cards
//                )
//            },
//            burracoDeck = deck,
//            playerDeck1 = playerDeck1,
//            playerDeck2 = playerDeck2,
//            discardPile = DiscardPile.create(listOf(discardCard)),
//            playerTurn = players.first().identity()
//        )
//    }

//    private fun areCardsDealt(): Boolean {
//        val numPlayerDeck1Cards = playerDeck1.numCards()
//        val numPlayerDeck2Cards = playerDeck2.numCards()
//        val numDeckCards = deck.numCards()
//        val numPlayersCards = players.fold(0) { sum, player -> sum + player.cards.size }
//        val numDiscardCard = if (discardCard == null) 0 else 1
//        val assignedCards = numDeckCards +
//                numPlayerDeck1Cards +
//                numPlayerDeck2Cards +
//                numPlayersCards +
//                numDiscardCard;
//
//        val result = assignedCards == totalCardsRequired
//        if (!result) {
//            log.warn(
//                "Total cards required are $totalCardsRequired but there are only $assignedCards cards \n" +
//                        "players: ${players.size} with $numPlayersCards cards \n" +
//                        "playerDeck1 with $numPlayerDeck1Cards cards \n" +
//                        "playerDeck2 with $numPlayerDeck2Cards cards \n" +
//                        "discardDeck with $numDiscardCard cards \n" +
//                        "deck with $numDeckCards cards \n"
//            )
//        }
//        return result
//    }

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