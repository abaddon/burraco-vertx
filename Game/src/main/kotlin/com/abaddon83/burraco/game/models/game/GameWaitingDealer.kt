package com.abaddon83.burraco.game.models.game

import com.abaddon83.burraco.game.events.game.*
import com.abaddon83.burraco.game.helpers.GameConfig
import com.abaddon83.burraco.game.helpers.GameConfig.deckSize
import com.abaddon83.burraco.game.helpers.playerCards
import com.abaddon83.burraco.game.helpers.updatePlayer
import com.abaddon83.burraco.game.helpers.validPlayer
import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.player.PlayerIdentity
import com.abaddon83.burraco.game.models.player.WaitingPlayer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

data class GameWaitingDealer private constructor(
    override val id: GameIdentity,
    override val version: Long,
    override val players: List<WaitingPlayer>,
    val deck: List<Card>,
    val playerDeck1: List<Card>,
    val playerDeck2: List<Card>,
    val discardDeck: List<Card>
) : Game() {
    override val log: Logger = LoggerFactory.getLogger(this::class.simpleName)


    companion object Factory {
        fun from(gameDraft: GameDraft): GameWaitingDealer =
            GameWaitingDealer(
                gameDraft.id,
                gameDraft.version,
                gameDraft.players,
                listOf(),
                listOf(),
                listOf(),
                listOf()
            )
    }

    fun addCardPlayer(playerIdentity: PlayerIdentity, card: Card): GameWaitingDealer {
        check(players.validPlayer(playerIdentity)) { "Player ${playerIdentity.valueAsString()} is not a player of the game ${id.valueAsString()}" }
        val maxCards= GameConfig.NUM_PLAYER_CARDS
        val numPlayerCards = players.playerCards(playerIdentity)!!.size + 1
        check(maxCards >= numPlayerCards) { "Player ${playerIdentity.valueAsString()} has already enough card. (Max $maxCards)" }
        return raiseEvent(CardAddedPlayer.create(id, playerIdentity, card)) as GameWaitingDealer
    }

    fun addCardFirstPlayerDeck(card: Card): GameWaitingDealer {
        val maxCards= GameConfig.FIRST_PLAYER_DECK_SIZE[players.size]!!
        check(maxCards >= playerDeck1.size + 1) { "The First player deck has already enough card. (Max $maxCards)" }
        return raiseEvent(CardAddedFirstPlayerDeck.create(id, card)) as GameWaitingDealer
    }

    fun addCardSecondPlayerDeck(card: Card): GameWaitingDealer {
        val maxCards= GameConfig.SECOND_PLAYER_DECK_SIZE
        check(maxCards >= playerDeck2.size + 1) { "The Second player deck has already enough card. (Max $maxCards)" }
        return raiseEvent(CardAddedSecondPlayerDeck.create(id, card)) as GameWaitingDealer
    }

    fun addCardDiscardDeck(card: Card): GameWaitingDealer {
        val maxCards= GameConfig.DISCARD_DECK_SIZE
        check(maxCards >= discardDeck.size + 1) { "The Discard deck has already enough card. (Max $maxCards)" }
        return raiseEvent(CardAddedDiscardDeck.create(id, card)) as GameWaitingDealer
    }

    fun addCardDeck(card: Card): GameWaitingDealer {
        val maxCards= deckSize(players.size)
        check(maxCards >= deck.size + 1) { "The Deck has already enough card. (Max $maxCards)" }
        return raiseEvent(CardAddedDeck.create(id, card)) as GameWaitingDealer
    }

    fun startGame(): GameExecutionPickUpPhase {
        val totalCards=listOf(deck.size, playerDeck1.size, playerDeck2.size, discardDeck.size)
            .plus(players.map { it.cards.size })
            .fold(0) { initial, value -> initial + value }
        check(totalCards == GameConfig.TOTAL_CARDS_REQUIRED){"The dealer has not finished dealing the cards"}
        return raiseEvent(GameStarted.create(id)) as GameExecutionPickUpPhase
    }

    private fun apply(event: CardAddedPlayer): GameWaitingDealer {
        log.debug("The aggregate is applying the event ${event::class.simpleName} with id ${event.messageId}")
        val updatedPlayers = players.updatePlayer(event.playerId) {
                player -> player.copy(cards = player.cards.plus(event.card))
        }
        log.debug("The Player ${event.playerId.valueAsString()} received a card, it has ${updatedPlayers.find { it.id == event.playerId }?.cards?.size} cards")
        return copy(players = updatedPlayers)
    }

    private fun apply(event: CardAddedFirstPlayerDeck): GameWaitingDealer {
        log.debug("The aggregate is applying the event ${event::class.simpleName} with id ${event.messageId}")
        val updatedPlayerDeck1=playerDeck1.plus(event.card)
        log.debug("The Player2 deck received a card, it has ${updatedPlayerDeck1.size} cards")
        return copy(playerDeck1 = updatedPlayerDeck1)
    }

    private fun apply(event: CardAddedSecondPlayerDeck): GameWaitingDealer {
        log.debug("The aggregate is applying the event ${event::class.simpleName} with id ${event.messageId}")
        val updatedPlayerDeck2 = playerDeck2.plus(event.card)
        log.debug("The Player2 deck received a card, it has ${updatedPlayerDeck2.size} cards")
        return copy(playerDeck2 = updatedPlayerDeck2)
    }

    private fun apply(event: CardAddedDiscardDeck): GameWaitingDealer {
        log.debug("The aggregate is applying the event ${event::class.simpleName} with id ${event.messageId}")
        val updatedDiscardDeck = discardDeck.plus(event.card)
        log.debug("The Discard deck received a card, it has ${updatedDiscardDeck.size} cards")
        return copy(discardDeck = updatedDiscardDeck)
    }

    private fun apply(event: CardAddedDeck): GameWaitingDealer {
        log.debug("The aggregate is applying the event ${event::class.simpleName} with id ${event.messageId}")
        val updatedDeck=deck.plus(event.card)
        log.debug("The Deck received a card, it has ${updatedDeck.size} cards")
        return copy(deck = updatedDeck)
    }

    private fun apply(event: GameStarted): GameExecutionPickUpPhase {
        log.debug("The aggregate is applying the event ${event::class.simpleName} with id ${event.messageId}")
        return GameExecutionPickUpPhase.from(this)
    }
}