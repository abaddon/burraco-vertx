//package com.abaddon83.burraco.game.models.burracoGameWaitingPlayers
//
//class BurracoDealerTest {
////    @Test
////    fun `check BurracoCardsDealt for 2 players`() {
////        val expectedDiscardPileSize = 1
////        val expectedFirstPozzettoSize = 11
////        val expectedSecondPozzettoSize = 11
////        val expectedDeckSize = 63
////        val expectedPlayerCards = 11
////        val expectedPlayers = 2
////
////        val game = BurracoGame.create(GameIdentity.create())
////                .addPlayer(BurracoPlayer(PlayerIdentity.create()))
////                .addPlayer(BurracoPlayer(PlayerIdentity.create()))
////
////        val burracoDealer = BurracoDealer.create(game.deck, game.players)
////
////        assert(burracoDealer.dealCardToDiscardPile.numCards() == expectedDiscardPileSize)
////        assert(burracoDealer.dealCardsToFirstMazzetto.numCards() == expectedFirstPozzettoSize)
////        assert(burracoDealer.dealCardsToSecondMazzetto.numCards() == expectedSecondPozzettoSize)
////        assert(burracoDealer.burracoDeck.numCards() == expectedDeckSize)
////        burracoDealer.dealCardsToPlayers.values.forEach { list ->
////            assert(list.size == expectedPlayerCards)
////        }
////        assert(burracoDealer.dealCardsToPlayers.size == expectedPlayers)
////
////    }
////
////    @Test
////    fun `check BurracoCardsDealt for 3 players`() {
////        val expectedDiscardPileSize = 1
////        val expectedFirstPozzettoSize = 18
////        val expectedSecondPozzettoSize = 11
////        val expectedDeckSize = 45
////        val expectedPlayerCards = 11
////        val expectedPlayers = 3
////
////        val game = BurracoGame.create(GameIdentity.create())
////                .addPlayer(BurracoPlayer(PlayerIdentity.create()))
////                .addPlayer(BurracoPlayer(PlayerIdentity.create()))
////                .addPlayer(BurracoPlayer(PlayerIdentity.create()))
////
////        val burracoDealer = BurracoDealer.create(game.deck, game.players)
////
////        assert(burracoDealer.dealCardToDiscardPile.numCards() == expectedDiscardPileSize)
////        assert(burracoDealer.dealCardsToFirstMazzetto.numCards() == expectedFirstPozzettoSize)
////        assert(burracoDealer.dealCardsToSecondMazzetto.numCards() == expectedSecondPozzettoSize)
////        assert(burracoDealer.burracoDeck.numCards() == expectedDeckSize)
////        burracoDealer.dealCardsToPlayers.values.forEach { list ->
////            assert(list.size == expectedPlayerCards)
////        }
////        assert(burracoDealer.dealCardsToPlayers.size == expectedPlayers)
////
////    }
////
////    @Test
////    fun `check BurracoCardsDealt for 4 players`() {
////        val expectedDiscardPileSize = 1
////        val expectedFirstPozzettoSize = 11
////        val expectedSecondPozzettoSize = 11
////        val expectedDeckSize = 41
////        val expectedPlayerCards = 11
////        val expectedPlayers = 4
////
////        val game = BurracoGame.create(GameIdentity.create())
////                .addPlayer(BurracoPlayer(PlayerIdentity.create()))
////                .addPlayer(BurracoPlayer(PlayerIdentity.create()))
////                .addPlayer(BurracoPlayer(PlayerIdentity.create()))
////                .addPlayer(BurracoPlayer(PlayerIdentity.create()))
////
////        val burracoDealer = BurracoDealer.create(game.deck, game.players)
////
////        assert(burracoDealer.dealCardToDiscardPile.numCards() == expectedDiscardPileSize)
////        assert(burracoDealer.dealCardsToFirstMazzetto.numCards() == expectedFirstPozzettoSize)
////        assert(burracoDealer.dealCardsToSecondMazzetto.numCards() == expectedSecondPozzettoSize)
////        assert(burracoDealer.burracoDeck.numCards() == expectedDeckSize)
////        burracoDealer.dealCardsToPlayers.values.forEach { list ->
////            assert(list.size == expectedPlayerCards)
////        }
////        assert(burracoDealer.dealCardsToPlayers.size == expectedPlayers)
////
////    }
//}