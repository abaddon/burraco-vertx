//package com.abaddon83.burraco.game.models
//
//import com.abaddon83.burraco.game.models.decks.ListCardsBuilder
//import com.abaddon83.burraco.game.models.decks.MazzettoDecks
//import com.abaddon83.burraco.game.models.decks.PlayerDeck
//import org.junit.jupiter.api.Test
//import kotlin.test.assertFailsWith
//
//class MazzettoDecksTest {
//
//    @Test
//    fun `Given 2 mazzettiDeck, when I create a MazzettoDecks, then I have it`() {
//        val mazzettoDeck1 = PlayerDeck.create(ListCardsBuilder.allRanksWithJollyCards().take(11))
//        val mazzettoDeck2 = PlayerDeck.create(ListCardsBuilder.allRanksWithJollyCards().take(11))
//        val list = listOf(mazzettoDeck1, mazzettoDeck2)
//
//        val expectedNumCards = 22
//
//        val actualMazzettoDecks = MazzettoDecks.create(list)
//        assert(actualMazzettoDecks.numCards() == expectedNumCards)
//    }
//
//    @Test
//    fun `Given 1 mazzettiDeck, when I create a MazzettoDecks, then I receive an error`() {
//        val mazzettoDeck1 = PlayerDeck.create(ListCardsBuilder.allRanksWithJollyCards().take(11))
//        val list = listOf(mazzettoDeck1)
//
//
//        assertFailsWith(IllegalArgumentException::class) {
//            MazzettoDecks.create(list)
//        }
//    }
//
//    @Test
//    fun `Given 3 mazzettiDeck, when I create a MazzettoDecks, then I receive an error`() {
//        val mazzettoDeck1 = PlayerDeck.create(ListCardsBuilder.allRanksWithJollyCards().take(11))
//        val list = listOf(mazzettoDeck1, mazzettoDeck1, mazzettoDeck1)
//
//        assertFailsWith(IllegalArgumentException::class) {
//            MazzettoDecks.create(list)
//        }
//    }
//
//    @Test
//    fun `Given a MazzettoDecks full, when ask the first Mazzetto, I receive the first`() {
//        val mazzettoDeck1 = PlayerDeck.create(ListCardsBuilder.allRanksWithJollyCards().take(11))
//        val mazzettoDeck2 = PlayerDeck.create(ListCardsBuilder.allRanksWithJollyCards().take(11))
//        assert(mazzettoDeck1 != mazzettoDeck2)
//        val list = listOf(mazzettoDeck1, mazzettoDeck2)
//
//        val expectedNumCards = 11
//
//        val mazzettoDecks = MazzettoDecks.create(list)
//        val actualMazzettoDeck = mazzettoDecks.firstMazzettoAvailable()
//        val actualMazzettoDecks = mazzettoDecks.mazzettoTaken(actualMazzettoDeck)
//
//        assert(actualMazzettoDeck == mazzettoDeck2)
//        assert(actualMazzettoDecks.firstMazzettoAvailable() != actualMazzettoDeck)
//        assert(actualMazzettoDecks.numCards() == expectedNumCards)
//    }
//
//    @Test
//    fun `Given a MazzettoDecks empty, when ask the first Mazzetto, I receive an error`() {
//        val mazzettoDeck1 = PlayerDeck.create(ListCardsBuilder.allRanksWithJollyCards().take(11))
//        val mazzettoDeck2 = PlayerDeck.create(ListCardsBuilder.allRanksWithJollyCards().take(11))
//        assert(mazzettoDeck1 != mazzettoDeck2)
//        val list = listOf(mazzettoDeck1, mazzettoDeck2)
//        val mazzettoDecks = MazzettoDecks.create(list)
//        val actualMazzettoDecks = mazzettoDecks
//                .mazzettoTaken(mazzettoDeck1)
//                .mazzettoTaken(mazzettoDeck2)
//
//        assertFailsWith(IllegalStateException::class) {
//            actualMazzettoDecks.firstMazzettoAvailable()
//        }
//    }
//
//}