package com.abaddon83.burraco.helpers

import com.abaddon83.burraco.game.events.game.*
import com.abaddon83.burraco.game.helpers.GameConfig
import com.abaddon83.burraco.game.helpers.GameConfig.deckSize
import com.abaddon83.burraco.game.helpers.removeCards
import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.card.Ranks
import com.abaddon83.burraco.game.models.card.Suits
import com.abaddon83.burraco.game.models.game.GameIdentity
import com.abaddon83.burraco.game.models.player.PlayerIdentity

object DeckHelper{

    fun oneFullDeckCards(): MutableList<Card>{
        val x = Suits.allSuit.map { suit ->
            Ranks.fullRanks.map { rank ->
                Card(suit,rank)
            }
        }.flatten().plus(listOf(
            Card(Suits.Jolly, Ranks.Jolly),
            Card(Suits.Jolly, Ranks.Jolly),
        ))
        return x.toMutableList()
    }

    fun generateFakeDealerEvents(gameId: GameIdentity, players:List<PlayerIdentity>, playerIdentitySelected: PlayerIdentity? = null, cardsSelected: List<Card> = listOf()):GameDecksHelper {
        val allCards = oneFullDeckCards().plus(oneFullDeckCards())
        assert(allCards.size == GameConfig.TOTAL_CARDS_REQUIRED)


        val availableCards = when(playerIdentitySelected){
            is PlayerIdentity -> allCards.removeCards(cardsSelected)
                else -> allCards
        }.toMutableList()

        //discardPile
        val discardPileEvent = listOf(CardDealtWithDiscardDeck.create(gameId,availableCards.removeFirst()))
        assert(discardPileEvent.size == GameConfig.DISCARD_DECK_SIZE)

        //players
        val playersCards = players.map { playerIdentity ->
            when(playerIdentity){
                playerIdentitySelected -> {
                    val numCards = GameConfig.NUM_PLAYER_CARDS - cardsSelected.size
                    cardsSelected
                        .map { card -> CardDealtWithPlayer.create(gameId,playerIdentity,card) }
                        .plus(
                            (1..numCards).map { CardDealtWithPlayer.create(gameId,playerIdentity,availableCards.removeFirst()) }
                        )
                    }
                else -> (1..GameConfig.NUM_PLAYER_CARDS).map { CardDealtWithPlayer.create(gameId,playerIdentity,availableCards.removeFirst()) }
            }

        }.flatten()
        assert(playersCards.size == GameConfig.NUM_PLAYER_CARDS * players.size)

        //playerDeck1
        val playerDeck1 =(1..GameConfig.FIRST_PLAYER_DECK_SIZE[players.size]!!).map { CardDealtWithFirstPlayerDeck.create(gameId,availableCards.removeFirst()) }
        assert(playerDeck1.size == GameConfig.FIRST_PLAYER_DECK_SIZE[players.size]!!)

        //playerDeck2
        val playerDeck2 =(1..GameConfig.SECOND_PLAYER_DECK_SIZE).map { CardDealtWithSecondPlayerDeck.create(gameId,availableCards.removeFirst()) }
        assert(playerDeck2.size == GameConfig.SECOND_PLAYER_DECK_SIZE)

        //deck
        val deck = (1..deckSize(players.size)).map { CardDealtWithDeck.create(gameId,availableCards.removeFirst()) }
        assert(deck.size == deckSize(players.size))
        assert(availableCards.isEmpty())

        val events=deck
            .plus(playerDeck2)
            .plus(playerDeck1)
            .plus(playersCards)
            .plus(discardPileEvent)

        assert(events.size == GameConfig.TOTAL_CARDS_REQUIRED)

        return GameDecksHelper(playersCards,deck,playerDeck1,playerDeck2,discardPileEvent)
    }

}