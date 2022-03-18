//package com.abaddon83.burraco.game.commands
//
////TODO Test to update to the new version
//class DropCardOnDiscardPileCmdTest{
////
////    @BeforeAll
////    fun loadEvents(){
////        eventStore.save(events){}
////    }
////
////    @Test
////    fun `Given a command to drop a card in the discard pile, when I execute the command, then the card is dropped`(){
////        val command = DropCardOnDiscardPileCmd(gameIdentity = gameIdentity,playerIdentity = playerIdentity1, card = burracoDeckCards[0])
////        assert(commandHandler.handle(command) is Valid)
////    }
////
////    @Test
////    fun `Given a command to drop a card already dropped, when I execute the command, then nothing happened`(){
////        val command = DropCardOnDiscardPileCmd(gameIdentity = gameIdentity,playerIdentity = playerIdentity1, card = burracoDeckCards[0])
////        assert(commandHandler.handle(command) is Valid)
////        assert(commandHandler.handle(command) is Invalid)
////    }
////
////    @Test
////    fun `Given a command to execute on a burraco game that doesn't exist, when I execute the command, then I receive an error`(){
////        val command = DropCardOnDiscardPileCmd(gameIdentity = GameIdentity.create(),playerIdentity = playerIdentity1, card = burracoDeckCards[0])
////        assert(commandHandler.handle(command) is Invalid)
////    }
////
////    val eventStore = EventStoreInMemoryBusAdapter()
////    private val commandHandler = CommandHandler(eventStore, FakeGameEventsBrokerProducer())
////    val deck = BurracoDeck.create()
////    val gameIdentity: GameIdentity = GameIdentity.create()
////    val aggregate = BurracoGame(identity = gameIdentity)
////    val playerIdentity1 = PlayerIdentity.create()
////    val playerIdentity2 = PlayerIdentity.create()
////
////    val allCards = ListCardsBuilder.allRanksWithJollyCards()
////            .plus(ListCardsBuilder.allRanksWithJollyCards())
////            .shuffled()
////    val cardsPlayer1 = Pair(playerIdentity1,allCards.take(11))
////    val cardsPlayer2 = Pair(playerIdentity2,allCards.take(11))
////
////    val mazzettoDeck1Cards = allCards.take(11)
////    val mazzettoDeck2Cards = allCards.take(11)
////
////    val discardPileCards = allCards.take(1)
////
////    val burracoDeckCards = allCards.take(108-1-11-11-11-11)
////
////    val playersCards = mapOf<PlayerIdentity,List<Card>>(cardsPlayer1,cardsPlayer2)
////
////    val events = listOf<Event>(
////            BurracoGameCreated(identity = gameIdentity),
////            PlayerAdded(identity = gameIdentity, playerIdentity = playerIdentity1),
////            PlayerAdded(identity = gameIdentity, playerIdentity = playerIdentity2),
////
//////        CardsDealtToPlayer(identity = gameIdentity,player = playerIdentity1,cards = playersCards[playerIdentity1] ?: error("playerIdentity1 not found")),
//////        CardsDealtToPlayer(identity = gameIdentity,player = playerIdentity2,cards = playersCards[playerIdentity2] ?: error("playerIdentity2 not found")),
////            GameInitialised(identity = gameIdentity, players = listOf(playerIdentity1,playerIdentity2)),
////            CardPickedFromDeck(identity = gameIdentity, playerIdentity = playerIdentity1, card = burracoDeckCards[0])
////    )
//}