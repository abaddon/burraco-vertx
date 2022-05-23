package com.abaddon83.burraco.dealer.helpers

import com.abaddon83.burraco.dealer.models.Card
import com.abaddon83.burraco.dealer.models.DealerConfig.NUM_RANK
import com.abaddon83.burraco.dealer.models.DealerConfig.NUM_SUIT
import com.abaddon83.burraco.dealer.models.DealerConfig.SINGLE_DECK_CARD
import com.abaddon83.burraco.dealer.models.DealerConfig.SINGLE_DECK_CARD_WITH_JOLLY
import com.abaddon83.burraco.dealer.models.Ranks
import com.abaddon83.burraco.dealer.models.Suits

object CardsHelper {

    fun allRanksWithJollyCards(): List<Card> {
        val cards = listOf(allRanksCards(), addJolly()).flatten()
        assert(cards.size == SINGLE_DECK_CARD_WITH_JOLLY)
        return cards.shuffled()
    }

    fun allRanksCards(): List<Card> {
        val cards = Suits.allSuit.map { suit -> buildCardSuit(suit, Ranks.fullRanks) }.flatten()
        assert(cards.size == SINGLE_DECK_CARD) { "The card list has to contain exactly 52 cards" }
        Suits.allSuit.forEach { suit ->
            assert(cards.count { card -> card.suit == suit } == NUM_SUIT) { "The card list doesn't contain $NUM_SUIT ${suit.icon} cards" }
        }
        Ranks.fullRanks.forEach { rank ->
            assert(cards.count { card -> card.rank == rank } == NUM_RANK) { "The card list doesn't contain $NUM_RANK ${rank.label} cards" }
        }
        return cards.shuffled()
    }

    fun addJolly(): List<Card> {
        val cards = listOf(Card(Suits.Jolly, Ranks.Jolly), Card(Suits.Jolly, Ranks.Jolly))
        assert(cards.size == 2) { "The card list has to contain exactly 2 cards" }
        assert(cards.count { card -> card.rank == Ranks.Jolly && card.suit == Suits.Jolly } == 2) { "The card list doesn't contain 2 ${Ranks.Jolly.label} cards" }
        return cards.shuffled()
    }

    private fun buildCardSuit(suit: Suits.Suit, ranks: List<Ranks.Rank>): List<Card> =
            ranks.map { rank -> Card(suit, rank) }.toList()

}