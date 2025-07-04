//package com.abaddon83.burraco.game.models.burracoGameExecutions
//
//class BurracoGameExecutionTurnEndTest {
//
////    @Test
////    fun `Given a player with 0 cards, when pickup the mazzetto, then receive the mazzetto cards`() {
////        val player1Id = PlayerIdentity.create()
////        val player1 = PlayerInGame(player1Id, listOf(), BurracoCardsOnTable(listOf(), listOf()), false)
////        val game = BurracoGameInitTurnTestFactory.create(player1Id = player1Id)
////                .setPlayer1(player1)
////                .buildTurnPhaseEnd()
////
////        val actualGame = game.pickupMazzetto(player1Id)
////
////        assert(actualGame.playerCards(player1Id).size == 11)
////        assert(actualGame.listOfPlayers().size == 2)
////    }
////
////    @Test
////    fun `Given a player with a card, when pickup the mazzetto, then receive an error`() {
////        val player1Id = PlayerIdentity.create()
////        val player1 = PlayerInGame(player1Id, listOf(Card(Suit.Heart, Rank.Six)), BurracoCardsOnTable(listOf(), listOf()), false)
////        val game = BurracoGameInitTurnTestFactory.create(player1Id = player1Id)
////                .setPlayer1(player1)
////                .buildTurnPhaseEnd()
////
////        assertFailsWith(IllegalStateException::class) {
////            game.pickupMazzetto(player1Id)
////        }
////    }
////
////    @Test
////    fun `Given a player with 0 cards and the pozzetto already taken, when pickup the mazzetto, then receive an error`() {
////        val player1Id = PlayerIdentity.create()
////        val player1 = PlayerInGame(player1Id, listOf(), BurracoCardsOnTable(listOf(), listOf()), true)
////        val game = BurracoGameInitTurnTestFactory.create(player1Id = player1Id)
////                .setPlayer1(player1)
////                .buildTurnPhaseEnd()
////
////        assertFailsWith(IllegalStateException::class) {
////            game.pickupMazzetto(player1Id)
////        }
////    }
////
////    @Test
////    fun `Given a player of another turn, when pickup the mazzetto, then receive an error`() {
////        val player1Id = PlayerIdentity.create()
////        val player1 = PlayerInGame(player1Id, listOf(), BurracoCardsOnTable(listOf(), listOf()), false)
////        val game = BurracoGameInitTurnTestFactory.create(player1Id = player1Id)
////                .setPlayer1(player1)
////                .setPlayer2Turn()
////                .buildTurnPhaseEnd()
////
////        assertFailsWith(IllegalStateException::class) {
////            game.pickupMazzetto(player1Id)
////        }
////    }
////
////    @Test
////    fun `Given a player of another game, when pickup the mazzetto, then receive an error`() {
////        val game = BurracoGameInitTurnTestFactory.create()
////                .buildTurnPhaseEnd()
////
////        assertFailsWith(IllegalStateException::class) {
////            game.pickupMazzetto(PlayerIdentity.create())
////        }
////    }
////
////    @Test
////    fun `Given a player than finished the turn, when confirm the turn is over, then it's the turn of the next player`() {
////        val player1Id = PlayerIdentity.create()
////        val player2Id = PlayerIdentity.create()
////        val game = BurracoGameInitTurnTestFactory.create(player1Id = player1Id, player2Id = player2Id)
////                .buildTurnPhaseEnd()
////
////        game.nextPlayerTurn(player1Id)
////    }
////
////    @Test
////    fun `Given a player during the turn of another player, when confirm the turn is over, then it's the turn of the next player`() {
////        val player1Id = PlayerIdentity.create()
////        val player2Id = PlayerIdentity.create()
////        val game = BurracoGameInitTurnTestFactory.create(player1Id = player1Id, player2Id = player2Id)
////                .setPlayer2Turn()
////                .buildTurnPhaseEnd()
////
////        assertFailsWith(IllegalStateException::class) {
////            game.nextPlayerTurn(player1Id)
////        }
////    }
////
////    @Test
////    fun `Given a player of another game, when confirm the turn is over, then it's the turn of the next player`() {
////        val player1Id = PlayerIdentity.create()
////        val player2Id = PlayerIdentity.create()
////        val game = BurracoGameInitTurnTestFactory.create(player1Id = player1Id, player2Id = player2Id)
////                .setPlayer2Turn()
////                .buildTurnPhaseEnd()
////
////        assertFailsWith(IllegalStateException::class) {
////            game.nextPlayerTurn(PlayerIdentity.create())
////        }
////    }
////
////    @Test
////    fun `Given a player with a Burraco, no cards, when complete the turn, then the game is end`() {
////        val player1Id = PlayerIdentity.create()
////        val tris = BurracoTris.create(listOf(
////                Card(Suit.Heart, Rank.Six), Card(Suit.Heart, Rank.Six),
////                Card(Suit.Pike, Rank.Six), Card(Suit.Pike, Rank.Six),
////                Card(Suit.Tile, Rank.Six), Card(Suit.Tile, Rank.Six),
////                Card(Suit.Clover, Rank.Six), Card(Suit.Clover, Rank.Six)
////        ))
////        val player1 = PlayerInGame(player1Id, listOf(), BurracoCardsOnTable(listOf(tris), listOf()), true)
////        val game = BurracoGameInitTurnTestFactory.create(player1Id = player1Id)
////                .setPlayer1(player1)
////                .buildTurnPhaseEnd()
////
////        val actualGame = game.completeGame(player1Id)
////
////
////    }
////
////    @Test
////    fun `Given a player with a Burraco, some cards, when complete the turn, then receive an error`() {
////        val player1Id = PlayerIdentity.create()
////        val tris = BurracoTris.create(listOf(
////                Card(Suit.Heart, Rank.Six), Card(Suit.Heart, Rank.Six),
////                Card(Suit.Pike, Rank.Six), Card(Suit.Pike, Rank.Six),
////                Card(Suit.Tile, Rank.Six), Card(Suit.Tile, Rank.Six),
////                Card(Suit.Clover, Rank.Six), Card(Suit.Clover, Rank.Six)
////        ))
////        val player1 = PlayerInGame(player1Id, listOf(Card(Suit.Heart, Rank.Four)), BurracoCardsOnTable(listOf(tris), listOf()), false)
////        val game = BurracoGameInitTurnTestFactory.create(player1Id = player1Id)
////                .setPlayer1(player1)
////                .buildTurnPhaseEnd()
////
////        assertFailsWith(IllegalStateException::class) {
////            game.completeGame(player1Id)
////        }
////    }
////
////    @Test
////    fun `Given a player with the mazzetto didn't take, when complete the turn, then receive an error`() {
////        val player1Id = PlayerIdentity.create()
////        val tris = BurracoTris.create(listOf(
////                Card(Suit.Heart, Rank.Six), Card(Suit.Heart, Rank.Six),
////                Card(Suit.Pike, Rank.Six), Card(Suit.Pike, Rank.Six),
////                Card(Suit.Tile, Rank.Six), Card(Suit.Tile, Rank.Six),
////                Card(Suit.Clover, Rank.Six), Card(Suit.Clover, Rank.Six)
////        ))
////        val player1 = PlayerInGame(player1Id, listOf(), BurracoCardsOnTable(listOf(tris), listOf()), false)
////        val game = BurracoGameInitTurnTestFactory.create(player1Id = player1Id)
////                .setPlayer1(player1)
////                .buildTurnPhaseEnd()
////
////        assertFailsWith(IllegalStateException::class) {
////            game.completeGame(player1Id)
////        }
////    }
////
////    @Test
////    fun `Given a player with not a burraco, when complete the turn, then receive an error`() {
////        val player1Id = PlayerIdentity.create()
////        val player1 = PlayerInGame(player1Id, listOf(), BurracoCardsOnTable(listOf(), listOf()), true)
////        val game = BurracoGameInitTurnTestFactory.create(player1Id = player1Id)
////                .setPlayer1(player1)
////                .buildTurnPhaseEnd()
////
////        assertFailsWith(IllegalStateException::class) {
////            game.completeGame(player1Id)
////        }
////    }
////
////    @Test
////    fun `Given a player during the turn of another player, when complete the turn, then receive an error`() {
////        val player1Id = PlayerIdentity.create()
////        val tris = BurracoTris.create(listOf(
////                Card(Suit.Heart, Rank.Six), Card(Suit.Heart, Rank.Six),
////                Card(Suit.Pike, Rank.Six), Card(Suit.Pike, Rank.Six),
////                Card(Suit.Tile, Rank.Six), Card(Suit.Tile, Rank.Six),
////                Card(Suit.Clover, Rank.Six), Card(Suit.Clover, Rank.Six)
////        ))
////        val player1 = PlayerInGame(player1Id, listOf(), BurracoCardsOnTable(listOf(tris), listOf()), true)
////        val game = BurracoGameInitTurnTestFactory.create(player1Id = player1Id)
////                .setPlayer1(player1)
////                .setPlayer2Turn()
////                .buildTurnPhaseEnd()
////
////        assertFailsWith(IllegalStateException::class) {
////            game.completeGame(player1Id)
////        }
////    }
////
////    @Test
////    fun `Given a player of another game, when complete the turn, then receive an error`() {
////        val player1Id = PlayerIdentity.create()
////        val game = BurracoGameInitTurnTestFactory.create(player1Id = player1Id)
////                .buildTurnPhaseEnd()
////
////        assertFailsWith(IllegalStateException::class) {
////            game.completeGame(PlayerIdentity.create())
////        }
////    }
//}