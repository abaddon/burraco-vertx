//package com.abaddon83.burraco.game.models.burracoGameExecutions
//
//class BurracoGameExecutionTurnExecutionTest {
//
////    @Test
////fun `Given a player during his turn, when drop a tris, the operation is executed`() {
////        val tris = BurracoTris(BurracoIdentity.create(), Rank.Ace, listOf(
////            Card(Suit.Heart, Rank.Ace), Card(Suit.Heart, Rank.Ace), Card(
////                Suit.Clover, Rank.Ace)
////        ))
////        val player1Id = PlayerIdentity.create()
////        val player1 = PlayerInGame(player1Id, tris.showCards(), BurracoCardsOnTable(listOf(), listOf()), false)
////        val game = BurracoGameInitTurnTestFactory.create(player1Id = player1Id)
////                .setPlayer1(player1)
////                .buildTurnPhaseExecution()
////
////        val gameActual = game.dropOnTableATris(player1Id, tris)
////
////        assert(gameActual.playerTrisOnTable(player1Id).size == 1)
////        assert(gameActual.playerCards(player1Id).isEmpty())
////
////    }
////
////    @Test
////fun `Given a player not in the turn, when drop a tris, I receive an error`() {
////        val tris = BurracoTris(BurracoIdentity.create(), Rank.Ace, listOf(
////            Card(Suit.Heart, Rank.Ace), Card(Suit.Heart, Rank.Ace), Card(
////                Suit.Clover, Rank.Ace)
////        ))
////        val player1Id = PlayerIdentity.create()
////        val player1 = PlayerInGame(player1Id, tris.showCards(), BurracoCardsOnTable(listOf(), listOf()), false)
////        val game = BurracoGameInitTurnTestFactory.create()
////                .setPlayer1(player1)
////                .setPlayer2Turn()
////                .buildTurnPhaseExecution()
////
////        assertFailsWith(IllegalStateException::class) {
////            game.dropOnTableATris(player1Id, tris)
////        }
////    }
////
////    @Test
////fun `Given a player not in this game, when drop a tris, I receive an error`() {
////        val tris = BurracoTris(BurracoIdentity.create(), Rank.Ace, listOf(
////            Card(Suit.Heart, Rank.Ace), Card(Suit.Heart, Rank.Ace), Card(
////                Suit.Clover, Rank.Ace)
////        ))
////        val game = BurracoGameInitTurnTestFactory.create()
////                .buildTurnPhaseExecution()
////
////        assertFailsWith(IllegalStateException::class) {
////            game.dropOnTableATris(PlayerIdentity.create(), tris)
////        }
////    }
////
////    @Test
////fun `Given a player during his turn, when drop a scale, the operation is executed`() {
////        val scale = BurracoScale.create(listOf(
////                Card(Suit.Heart, Rank.Eight),
////                Card(Suit.Heart, Rank.Seven),
////                Card(Suit.Heart, Rank.Six),
////                Card(Suit.Heart, Rank.Five)
////        ))
////        val player1Id = PlayerIdentity.create()
////        val player1 = PlayerInGame(player1Id, scale.showCards(), BurracoCardsOnTable(listOf(), listOf()), false)
////        val game = BurracoGameInitTurnTestFactory.create(player1Id = player1Id)
////                .setPlayer1(player1)
////                .buildTurnPhaseExecution()
////
////        val gameActual = game.dropOnTableAScale(player1Id, scale)
////
////        assert(gameActual.playerScalesOnTable(player1Id).size == 1)
////        assert(gameActual.playerCards(player1Id).isEmpty())
////
////    }
////
////    @Test
////fun `Given a player not in the turn, when drop a scale, I receive an error`() {
////        val scale = BurracoScale.create(listOf(
////                Card(Suit.Heart, Rank.Eight),
////                Card(Suit.Heart, Rank.Seven),
////                Card(Suit.Heart, Rank.Six),
////                Card(Suit.Heart, Rank.Five)
////        ))
////        val player1Id = PlayerIdentity.create()
////        val player1 = PlayerInGame(player1Id, scale.showCards(), BurracoCardsOnTable(listOf(), listOf()), false)
////        val game = BurracoGameInitTurnTestFactory.create(player1Id = player1Id)
////                .setPlayer1(player1)
////                .setPlayer2Turn()
////                .buildTurnPhaseExecution()
////
////        assertFailsWith(IllegalStateException::class) {
////            game.dropOnTableAScale(player1Id, scale)
////        }
////    }
////
////    @Test
////fun `Given a player not in this game, when drop a scale, I receive an error`() {
////        val scale = BurracoScale.create(listOf(
////                Card(Suit.Heart, Rank.Eight),
////                Card(Suit.Heart, Rank.Seven),
////                Card(Suit.Heart, Rank.Six),
////                Card(Suit.Heart, Rank.Five)
////        ))
////        val game = BurracoGameInitTurnTestFactory.create()
////                .buildTurnPhaseExecution()
////
////        assertFailsWith(IllegalStateException::class) {
////            game.dropOnTableAScale(PlayerIdentity.create(), scale)
////        }
////    }
////
////    @Test
////fun `Given a player during his turn with a tris, when append a card on the tris, the operation is executed`() {
////        val tris = BurracoTris(BurracoIdentity.create(), Rank.Ace, listOf(
////            Card(Suit.Heart, Rank.Ace), Card(Suit.Heart, Rank.Ace), Card(
////                Suit.Clover, Rank.Ace)
////        ))
////        val player1Id = PlayerIdentity.create()
////        val cardToAppend = listOf(Card(Suit.Tile, Rank.Ace))
////        val player1 = PlayerInGame(player1Id, cardToAppend, BurracoCardsOnTable(listOfTris = listOf(tris), listOfScale = listOf()), false)
////        val game = BurracoGameInitTurnTestFactory.create(player1Id = player1Id)
////                .setPlayer1(player1)
////                .buildTurnPhaseExecution()
////
////
////        val exceptedTrisSize = tris.numCards() + cardToAppend.size
////
////        val gameActual = game.appendCardsOnABurracoDropped(player1Id, cardToAppend,tris.identity())
////
////        assert(gameActual.playerTrisOnTable(player1Id).size == 1)
////        assert(gameActual.playerTrisOnTable(player1Id).first().numCards() == exceptedTrisSize)
////        assert(gameActual.playerCards(player1Id).isEmpty())
////
////    }
////
////    @Test
////fun `Given a player with a tris and during the turn of another player, when append a card on the tris, I receive an error`() {
////        val tris = BurracoTris(BurracoIdentity.create(), Rank.Ace, listOf(
////            Card(Suit.Heart, Rank.Ace), Card(Suit.Heart, Rank.Ace), Card(
////                Suit.Clover, Rank.Ace)
////        ))
////        val player1Id = PlayerIdentity.create()
////        val cardToAppend = listOf(Card(Suit.Tile, Rank.Ace))
////        val player1 = PlayerInGame(player1Id, cardToAppend, BurracoCardsOnTable(listOfTris = listOf(tris), listOfScale = listOf()), false)
////        val game = BurracoGameInitTurnTestFactory.create()
////                .setPlayer1(player1)
////                .setPlayer2Turn()
////                .buildTurnPhaseExecution()
////
////        assertFailsWith(IllegalStateException::class) {
////            game.appendCardsOnABurracoDropped(player1Id, cardToAppend,tris.identity())
////        }
////
////    }
////
////    @Test
////fun `Given a player of another game, with a tris, when append a card on the tris, I receive an error`() {
////        val tris = BurracoTris(BurracoIdentity.create(), Rank.Ace, listOf(
////            Card(Suit.Heart, Rank.Ace), Card(Suit.Heart, Rank.Ace), Card(
////                Suit.Clover, Rank.Ace)
////        ))
////        val player1Id = PlayerIdentity.create()
////        val cardToAppend = listOf(Card(Suit.Tile, Rank.Ace))
////        val player1 = PlayerInGame(player1Id, cardToAppend, BurracoCardsOnTable(listOfTris = listOf(tris), listOfScale = listOf()), false)
////        val game = BurracoGameInitTurnTestFactory.create()
////                .setPlayer1(player1)
////                .buildTurnPhaseExecution()
////
////        assertFailsWith(IllegalStateException::class) {
////            game.appendCardsOnABurracoDropped(PlayerIdentity.create(), cardToAppend,tris.identity())
////        }
////    }
////
////
////    @Test
////fun `Given a player during his turn with no cards, when pickUp the mazzetto, then I can see the mazzetto cards on my hand`() {
////        val player1Id = PlayerIdentity.create()
////        val player1 = PlayerInGame(player1Id, listOf(), BurracoCardsOnTable(listOfTris = listOf(), listOfScale = listOf()), false)
////        val game = BurracoGameInitTurnTestFactory.create(player1Id = player1Id)
////                .setPlayer1(player1)
////                .buildTurnPhaseExecution()
////
////        val actualGame = game.pickupMazzetto(player1Id)
////        assert(actualGame.playerCards(player1Id).size == 11)
////
////    }
////
////    @Test
////fun `Given a player during his turn with some cards, when pickUp the mazzetto, then I receive an error`() {
////        val player1Id = PlayerIdentity.create()
////        val player1 = PlayerInGame(player1Id, listOf(Card(Suit.Heart, Rank.Ace)), BurracoCardsOnTable(listOfTris = listOf(), listOfScale = listOf()), false)
////        val game = BurracoGameInitTurnTestFactory.create()
////                .setPlayer1(player1)
////                .buildTurnPhaseExecution()
////
////        assertFailsWith(IllegalStateException::class){
////            game.pickupMazzetto(player1Id)
////        }
////    }
////
////    @Test
////fun `Given a player during his turn with no cards and a mazzetto already taken, when pickUp the mazzetto, then I receive an error`() {
////        val player1Id = PlayerIdentity.create()
////        val player1 = PlayerInGame(player1Id, listOf(Card(Suit.Heart, Rank.Ace)), BurracoCardsOnTable(listOfTris = listOf(), listOfScale = listOf()), true)
////        val game = BurracoGameInitTurnTestFactory.create()
////                .setPlayer1(player1)
////                .buildTurnPhaseExecution()
////
////        assertFailsWith(IllegalStateException::class){
////            game.pickupMazzetto(player1Id)
////        }
////    }
////
////    @Test
////fun `Given a player with no cards, during the tun of another player , when pickUp the mazzetto, then I receive an error`() {
////        val player1Id = PlayerIdentity.create()
////        val player1 = PlayerInGame(player1Id, listOf(), BurracoCardsOnTable(listOfTris = listOf(), listOfScale = listOf()), false)
////        val game = BurracoGameInitTurnTestFactory.create()
////                .setPlayer1(player1)
////                .setPlayer2Turn()
////                .buildTurnPhaseExecution()
////
////        assertFailsWith(IllegalStateException::class){
////            game.pickupMazzetto(player1Id)
////        }
////    }
////
////    @Test
////fun `Given a player of another game , when pickUp the mazzetto, then I receive an error`() {
////        val player1Id = PlayerIdentity.create()
////        val player1 = PlayerInGame(player1Id, listOf(), BurracoCardsOnTable(listOfTris = listOf(), listOfScale = listOf()), false)
////        val game = BurracoGameInitTurnTestFactory.create()
////                .setPlayer1(player1)
////                .setPlayer2Turn()
////                .buildTurnPhaseExecution()
////
////        assertFailsWith(IllegalStateException::class){
////            game.pickupMazzetto(PlayerIdentity.create())
////        }
////    }
////
////    @Test
////fun `Given a player during his turn with some cards, when drop a card on a discard pile, then I have a card less`() {
////        val player1Id = PlayerIdentity.create()
////        val cardToDrop = Card(Suit.Heart, Rank.Six)
////        val player1 = PlayerInGame(player1Id, listOf(cardToDrop), BurracoCardsOnTable(listOfTris = listOf(), listOfScale = listOf()), false)
////        val game = BurracoGameInitTurnTestFactory.create(player1Id = player1Id)
////                .setPlayer1(player1)
////                .buildTurnPhaseExecution()
////
////        val expectedDiscardPileSize = game.showDiscardPile().size + 1
////
////        val actualGame = game.dropCardOnDiscardPile(player1Id,cardToDrop)
////
////        assert(actualGame.playerCards(player1Id).isEmpty())
////        assert(actualGame.showDiscardPile().size == expectedDiscardPileSize)
////        assert(actualGame.showDiscardPile().contains(cardToDrop))
////
////    }
////
////    @Test
////fun `Given a player during the turn of another player, when drop a card on a discard pile, then receive an error`() {
////        val player1Id = PlayerIdentity.create()
////        val cardToDrop = Card(Suit.Heart, Rank.Six)
////        val player1 = PlayerInGame(player1Id, listOf(cardToDrop), BurracoCardsOnTable(listOfTris = listOf(), listOfScale = listOf()), false)
////        val game = BurracoGameInitTurnTestFactory.create()
////                .setPlayer1(player1)
////                .setPlayer2Turn()
////                .buildTurnPhaseExecution()
////
////        assertFailsWith(IllegalStateException::class){
////            game.dropCardOnDiscardPile(player1Id,cardToDrop)
////        }
////
////    }
//}