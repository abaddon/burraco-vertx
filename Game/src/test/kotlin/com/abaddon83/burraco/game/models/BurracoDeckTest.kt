//package com.abaddon83.burraco.game.models
//
//import com.abaddon83.burraco.common.models.valueObjects.Card
//import com.abaddon83.burraco.common.models.valueObjects.Ranks
//import com.abaddon83.burraco.common.models.valueObjects.Suits
//import com.abaddon83.burraco.game.models.decks.Deck
//import org.junit.jupiter.api.Test
//import kotlin.test.assertFailsWith
//
//class BurracoDeckTest {
//    @Test
//fun `Given a Burraco Deck, when I count the carts, then the carts are 108`() {
//        val deck = Deck.create()
//
//        assert(deck.numCards() == 108)
//
//        val listOfAllCards = mutableListOf<Card>()
//        while(deck.numCards()>0){
//            listOfAllCards.add(deck.grabFirstCard())
//        }
//
//        assert(listOfAllCards.size == 108)
//        assert(listOfAllCards.count{card -> card.rank == Rank.Jolly && card.suit == Suit.Jolly} == 4) { "The deck doesn't contain 4 ${Rank.Jolly.label}" }
//        Suit.allSuit.forEach{ suit -> assert(listOfAllCards.count{ card -> card.suit == suit} == 26) { "The card list doesn't contain 26 ${suit.icon} cards" }}
//        Rank.fullRank.forEach{ rank -> assert(listOfAllCards.count{ card -> card.rank == rank} == 8) { "The card list doesn't contain 8 ${rank.label} cards" }}
//    }
//
//    @Test
//fun `Given a Burraco Deck, when I shuffle it, then cards order is changed`() {
//        val deck = Deck.create()
//        val firstCard = deck.grabFirstCard()
//
//        val actualDeck = deck.shuffle()
//        val actualFirstCard = deck.grabFirstCard()
//
//        assert(firstCard != actualFirstCard)
//    }
//
//    @Test
//fun `Given a Burraco Deck, when I grab all cards, then I receive an error`() {
//        val deck = Deck.create()
//        assertFailsWith(UnsupportedOperationException::class) {
//            deck.grabAllCards()
//        }
//    }
//
//    @Test
//fun `Given a Burraco Deck, when I grab the first card, then I have a card`() {
//        val deck = Deck.create()
//
//        deck.grabFirstCard()
//
//        assert(deck.numCards() == 107)
//    }
//}