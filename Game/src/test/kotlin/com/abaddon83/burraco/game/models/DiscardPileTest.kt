//package com.abaddon83.burraco.game.models
//
//import com.abaddon83.burraco.common.models.valueObjects.Card
//import com.abaddon83.burraco.common.models.valueObjects.Ranks
//import com.abaddon83.burraco.common.models.valueObjects.Suits
//import com.abaddon83.burraco.game.models.decks.DiscardPile
//import org.junit.jupiter.api.Test
//import kotlin.test.assertFailsWith
//
//class DiscardPileTest {
//
//    @Test
//    fun `Given a list of card, when I create a discard pile, then I see the same cards in the discard pile`() {
//        val cardList = listOf(Card(Suit.Heart, Rank.Ace), Card(Suit.Heart, Rank.King))
//        val discardPile = DiscardPile.create(cardList)
//        val expectedSize = cardList.size
//
//        assert(discardPile.numCards() == expectedSize)
//    }
//
//    @Test
//    fun `Given a card, when I add it on the discard pile, then I see the card in the pile`() {
//        val cardList = listOf(Card(Suit.Heart, Rank.Ace), Card(Suit.Heart, Rank.King))
//        val discardPile = DiscardPile.create(cardList)
//
//        val cardToAdd = Card(Suit.Jolly, Rank.Jolly)
//
//        val expectedSize = cardList.size + 1
//
//        val discardPileWithCardAdded = discardPile.addCard(cardToAdd)
//
//        assert(discardPileWithCardAdded.numCards() == expectedSize)
//        assert(discardPileWithCardAdded.grabAllCards().contains(cardToAdd))
//    }
//
//
//    @Test
//    fun `given a discardPile, when I grab all cards, then the pile is empty`() {
//        val discardPile = DiscardPile.create(listOf(Card(Suit.Heart, Rank.Ace), Card(Suit.Heart, Rank.King)))
//        assert(discardPile.numCards() == 2)
//
//        val grabbedCards = discardPile.grabAllCards()
//
//        val expectedCardsInthePile = 0
//        val expectedCardsgrabbed = 2
//
//        assert(discardPile.numCards() == expectedCardsInthePile)
//        assert(grabbedCards.size == expectedCardsgrabbed)
//    }
//
//    @Test
//    fun `given an empty discardPile, when I grab all cards, then receive an error`() {
//
//        val discardPile = DiscardPile.create(listOf())
//
//        assertFailsWith(IllegalStateException::class) {
//            discardPile.grabAllCards()
//        }
//    }
//
//    @Test
//    fun `given a discardPile, when I grab only a card, then receive an error`() {
//
//        val discardPile = DiscardPile.create(listOf(Card(Suit.Heart, Rank.Ace), Card(Suit.Heart, Rank.King)))
//
//        assertFailsWith(UnsupportedOperationException::class) {
//            discardPile.grabFirstCard()
//        }
//    }
//
//}