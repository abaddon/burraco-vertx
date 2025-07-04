package com.abaddon83.burraco.helper

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.card.Rank
import com.abaddon83.burraco.common.models.card.Ranks
import com.abaddon83.burraco.common.models.card.Suit
import com.abaddon83.burraco.common.models.card.Suits
import com.abaddon83.burraco.common.models.event.game.CardAddedDeck
import com.abaddon83.burraco.common.models.event.game.CardAddedDiscardDeck
import com.abaddon83.burraco.common.models.event.game.CardAddedFirstPlayerDeck
import com.abaddon83.burraco.common.models.event.game.CardAddedPlayer
import com.abaddon83.burraco.common.models.event.game.CardAddedSecondPlayerDeck
import com.abaddon83.burraco.game.helpers.GameConfig
import com.abaddon83.burraco.game.helpers.GameConfig.deckSize
import com.abaddon83.burraco.game.helpers.removeCards
import com.abaddon83.burraco.common.models.card.Card

object DeckHelper {

    fun oneFullDeckCards(): MutableList<Card> {
        val x = Suits.allSuit.map { suit ->
            Ranks.fullRanks.map { rank ->
                Card(suit, rank)
            }
        }.flatten().plus(
            listOf(
                Card(Suit.Jolly, Rank.Jolly),
                Card(Suit.Jolly, Rank.Jolly),
            )
        )
        return x.toMutableList()
    }


    fun generateFakeDealerEvents(
        gameId: GameIdentity,
        players: List<PlayerIdentity>,
        playerIdentitySelected: PlayerIdentity? = null,
        cardsSelected: List<Card> = listOf(),
        discardDeckCard: Card? = null,
        playerDeck1Selected: List<Card> = listOf()
    ): GameDecksHelper {
        val allCards = oneFullDeckCards().plus(oneFullDeckCards())
        assert(allCards.size == GameConfig.TOTAL_CARDS_REQUIRED)


//        val availableCards = when(playerIdentitySelected){
//            is PlayerIdentity -> allCards.removeCards(cardsSelected)
//                else -> allCards
//        }.toMutableList()

        val availableCards = allCards
            .removeCards(cardsSelected)
            .removeCards(if (discardDeckCard == null) listOf() else listOf(discardDeckCard))
            .removeCards(playerDeck1Selected)
            .toMutableList()

        //discardPile
        val discardPileEvent =
            listOf(CardAddedDiscardDeck.create(gameId, discardDeckCard ?: availableCards.removeFirst()))
        assert(discardPileEvent.size == GameConfig.DISCARD_DECK_SIZE)

        //players
        val playersCards = players.map { playerIdentity ->
            when (playerIdentity) {
                playerIdentitySelected -> {
                    val numCards = GameConfig.NUM_PLAYER_CARDS - cardsSelected.size
                    cardsSelected
                        .map { card -> CardAddedPlayer.create(gameId, playerIdentity, card) }
                        .plus(
                            (1..numCards).map {
                                CardAddedPlayer.create(
                                    gameId,
                                    playerIdentity,
                                    availableCards.removeFirst()
                                )
                            }
                        )
                }

                else -> (1..GameConfig.NUM_PLAYER_CARDS).map {
                    CardAddedPlayer.create(
                        gameId,
                        playerIdentity,
                        availableCards.removeFirst()
                    )
                }
            }

        }.flatten()
        assert(playersCards.size == GameConfig.NUM_PLAYER_CARDS * players.size)

        //playerDeck1
        val playerDeck1RemainingCards = GameConfig.FIRST_PLAYER_DECK_SIZE[players.size]!! - playerDeck1Selected.size
        assert(playerDeck1RemainingCards >= 0)
        val playerDeck1 = playerDeck1Selected
            .map { card -> CardAddedFirstPlayerDeck.create(gameId, card) }
            .plus((1..playerDeck1RemainingCards).map {
                CardAddedFirstPlayerDeck.create(
                    gameId,
                    availableCards.removeFirst()
                )
            })
        assert(playerDeck1.size == GameConfig.FIRST_PLAYER_DECK_SIZE[players.size]!!)

        //playerDeck2
        val playerDeck2 = (1..GameConfig.SECOND_PLAYER_DECK_SIZE).map {
            CardAddedSecondPlayerDeck.create(
                gameId,
                availableCards.removeFirst()
            )
        }
        assert(playerDeck2.size == GameConfig.SECOND_PLAYER_DECK_SIZE)

        //deck
        val deck = (1..deckSize(players.size)).map { CardAddedDeck.create(gameId, availableCards.removeFirst()) }
        assert(deck.size == deckSize(players.size))
        assert(availableCards.isEmpty())

        val events = deck
            .plus(playerDeck2)
            .plus(playerDeck1)
            .plus(playersCards)
            .plus(discardPileEvent)

        assert(events.size == GameConfig.TOTAL_CARDS_REQUIRED)

        return GameDecksHelper(playersCards, deck, playerDeck1, playerDeck2, discardPileEvent)
    }

}