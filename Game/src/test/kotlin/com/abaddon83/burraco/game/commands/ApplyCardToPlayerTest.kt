//package com.abaddon83.burraco.game.commands
//
//import com.abaddon83.burraco.common.events.BurracoGameCreated
//import com.abaddon83.burraco.common.events.GameInitialised
//import com.abaddon83.burraco.common.events.PlayerAdded
//import com.abaddon83.burraco.common.models.identities.GameIdentity
//import com.abaddon83.burraco.common.models.identities.PlayerIdentity
//import com.abaddon83.burraco.common.models.valueObjects.Card
//import com.abaddon83.burraco.common.models.valueObjects.Ranks
//import com.abaddon83.burraco.common.models.valueObjects.Suits
//import com.abaddon83.utils.ddd.Event
//import com.abaddon83.utils.functionals.Invalid
//import com.abaddon83.utils.functionals.Valid
//import com.abaddon83.burraco.game.adapters.eventBrokerProducer.FakeGameEventsBrokerProducer
//import com.abaddon83.burraco.game.adapters.eventStoreAdapter.inMemory.EventStoreInMemoryBusAdapter
//import com.abaddon83.burraco.game.models.game.GameWaitingDealer
//import org.junit.jupiter.api.BeforeAll
//import org.junit.jupiter.api.Test
//
//internal class ApplyCardToPlayerTest{
//    companion object {
//        private val eventStore = EventStoreInMemoryBusAdapter()
//        private val commandHandler = CommandHandler(eventStore, FakeGameEventsBrokerProducer())
//        private val gameIdentity: GameIdentity = GameIdentity.create()
//        val playerIdentity1 = PlayerIdentity.create()
//        val playerIdentity2 = PlayerIdentity.create()
//        val events = listOf<Event>(
//            BurracoGameCreated(identity = gameIdentity),
//            PlayerAdded(identity = gameIdentity, playerIdentity = playerIdentity1),
//            PlayerAdded(identity = gameIdentity, playerIdentity = playerIdentity2),
//            GameInitialised(identity = gameIdentity,players = listOf(playerIdentity1, playerIdentity2))
//        )
//
//        @BeforeAll
//        @JvmStatic
//        fun beforeAll() {
//            eventStore.save(events) {}
//        }
//    }
//
//    @Test
//    fun `Given a game initialised, when I execute the command ApplyCardToPlayer, then the card is assigned to the player`(){
//        val command = ApplyCardToPlayer(gameIdentity = gameIdentity,playerIdentity = playerIdentity2,card = Card(Suits.Heart, Ranks.Ace))
//        commandHandler.handle(command).future()
//            .onSuccess { assert(it is Valid) }
//            .onFailure { assert(false) }
//    }
//
//    @Test
//    fun `Given a game initialised with a player with less then 11 cards, when I execute the command ApplyCardToPlayer to get 11 cards, then I the player has 11 cards`(){
//        val command = ApplyCardToPlayer(gameIdentity = gameIdentity,playerIdentity = playerIdentity1,card = Card(Suits.Heart, Ranks.Ace))
//        var keepRunning = true;
//        while(keepRunning) {
//            commandHandler.handle(command).future()
//                .onSuccess { cmdResult ->
//                    assert(cmdResult is Valid)
//                    val cardSize = ((cmdResult as Valid).value.game as GameWaitingDealer).players.find { burracoPlayer ->  burracoPlayer.identity() == playerIdentity1}!!.cards.size
//                    if(cardSize == 11){
//                        keepRunning = false
//                        assert(true)
//                    }
//                }
//                .onFailure { assert(false) }
//        }
//    }
//
//    @Test
//    fun `Given a game initialised with a player with 11 cards, when I execute the command ApplyCardToPlayer, then I receive an error`(){
//        val command = ApplyCardToPlayer(gameIdentity = gameIdentity,playerIdentity = playerIdentity1,card = Card(Suits.Heart, Ranks.Ace))
//
//        commandHandler.handle(command).future()
//            .onSuccess { cmdResult ->
//                assert(cmdResult is Invalid)
//            }
//            .onFailure { assert(false) }
//
//    }
//}