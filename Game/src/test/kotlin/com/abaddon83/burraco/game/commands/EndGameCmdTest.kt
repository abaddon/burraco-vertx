//package com.abaddon83.burraco.game.commands
//
////TODO Test to update to the new version
//class EndGameCmdTest {
////
////    @BeforeAll
////    fun loadEvents() {
////        eventStore.save(events)
////    }
////
////    @Test
////    fun `Given a command to end game, when I execute the command, then the game is ended`() {
////        val command = EndGameCmd(gameIdentity = gameIdentity, playerIdentity = playerIdentity1)
////        assert(commandHandler.handle(command) is Valid)
////    }
////
////    @Test
////    fun `Given a command to execute on a burraco game that doesn't exist, when I execute the command, then I receive an error`() {
////        val command = EndGameCmd(gameIdentity = GameIdentity.create(), playerIdentity = playerIdentity1)
////        assert(commandHandler.handle(command) is Invalid)
////    }
////
////    val eventStore = EventStoreInMemoryBusAdapter()
////    private val commandHandler = CommandHandler(eventStore,FakeGameEventsBrokerProducer())
////    val deck = BurracoDeck.create()
////    val gameIdentity: GameIdentity = GameIdentity.create()
////    val aggregate = BurracoGame(identity = gameIdentity)
////    val playerIdentity1 = PlayerIdentity.create()
////    val playerIdentity2 = PlayerIdentity.create()
////
////    val allCards = ListCardsBuilder.allRanksWithJollyCards()
////        .plus(ListCardsBuilder.allRanksWithJollyCards())
////        .shuffled()
////    val burracoScale = BurracoScale(
////        BurracoIdentity.create(),
////        Suits.Tile,
////        listOf(
////            Card(Suits.Tile, rank = Ranks.Three),
////            Card(Suits.Tile, rank = Ranks.Four),
////            Card(Suits.Tile, rank = Ranks.Five),
////            Card(Suits.Tile, rank = Ranks.Six),
////            Card(Suits.Tile, rank = Ranks.Seven),
////            Card(Suits.Tile, rank = Ranks.Eight),
////            Card(Suits.Tile, rank = Ranks.Nine),
////            Card(Suits.Tile, rank = Ranks.Ten),
////            Card(Suits.Tile, rank = Ranks.Jack),
////            Card(Suits.Tile, rank = Ranks.Queen),
////            Card(Suits.Tile, rank = Ranks.King),
////            Card(Suits.Tile, rank = Ranks.Ace)
////
////        )
////    )
////
////    val burracoScale2 = BurracoScale(
////        BurracoIdentity.create(),
////        Suits.Tile,
////        listOf(
////            Card(Suits.Pike, rank = Ranks.Three),
////            Card(Suits.Pike, rank = Ranks.Four),
////            Card(Suits.Pike, rank = Ranks.Five),
////            Card(Suits.Pike, rank = Ranks.Six),
////            Card(Suits.Pike, rank = Ranks.Seven),
////            Card(Suits.Pike, rank = Ranks.Eight),
////            Card(Suits.Pike, rank = Ranks.Nine),
////            Card(Suits.Pike, rank = Ranks.Ten),
////            Card(Suits.Pike, rank = Ranks.Jack),
////            Card(Suits.Pike, rank = Ranks.Queen)
////        )
////    )
////
////    val cardsPlayer1 = Pair(playerIdentity1, burracoScale.showCards().take(11))
////    val cardsPlayer2 = Pair(playerIdentity2, allCards.take(11))
////
////    val mazzettoDeck1Cards = burracoScale2.showCards().plus(Card(Suits.Clover, Ranks.Five))
////    val mazzettoDeck2Cards = allCards.take(11)
////
////    val discardPileCards = burracoScale.showCards().takeLast(1)
////    val burracoDeckNumCard =
////        108 - mazzettoDeck1Cards.size - mazzettoDeck2Cards.size - cardsPlayer1.second.size - cardsPlayer2.second.size - discardPileCards.size
////    val burracoDeckCards = allCards.take(burracoDeckNumCard)
////
////    val playersCards = mapOf<PlayerIdentity, List<Card>>(cardsPlayer1, cardsPlayer2)
////
////    val events = listOf<Event>(
////        BurracoGameCreated(identity = gameIdentity),
////        PlayerAdded(identity = gameIdentity, playerIdentity = playerIdentity1),
////        PlayerAdded(identity = gameIdentity, playerIdentity = playerIdentity2),
////        GameInitialised(identity = gameIdentity, players = listOf(playerIdentity1,playerIdentity2)),
////
////        CardsPickedFromDiscardPile(identity = gameIdentity, playerIdentity = playerIdentity1, cards = discardPileCards),
////        ScaleDropped(identity = gameIdentity, playerIdentity = playerIdentity1, scale = burracoScale),
////        MazzettoPickedUp(identity = gameIdentity, playerIdentity = playerIdentity1, mazzettoDeck = mazzettoDeck1Cards),
////        ScaleDropped(identity = gameIdentity, playerIdentity = playerIdentity1, scale = burracoScale2),
////        CardDroppedIntoDiscardPile(
////            identity = gameIdentity, playerIdentity = playerIdentity1, card = Card(
////                Suits.Clover,
////                Ranks.Five
////            )
////        )
////    )
//
//}