package com.abaddon83.burraco.game.commands

import com.abaddon83.utils.ddd.Event
import com.abaddon83.burraco.game.adapters.eventStoreAdapter.inMemory.EventStoreInMemoryBusAdapter
import com.abaddon83.burraco.common.events.*
import com.abaddon83.burraco.game.models.BurracoDeck
import com.abaddon83.burraco.game.models.BurracoGame
import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.burraco.game.models.decks.ListCardsBuilder
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.utils.functionals.Invalid
import com.abaddon83.utils.functionals.Valid
import com.abaddon83.burraco.game.adapters.eventBrokerProducer.FakeGameEventsBrokerProducer
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
//TODO Test to update to the new version
class EndPlayerTurnCmdTest {
//
//    @BeforeAll
//    fun loadEvents() {
//        eventStore.save(events)
//    }
//
//    @Test
//    fun `Given a command to end player turn, when I execute the command, then the player turn is ended`() {
//        val command = EndPlayerTurnCmd(gameIdentity = gameIdentity, playerIdentity = playerIdentity1)
//        assert(commandHandler.handle(command) is Valid)
//    }
//
//    @Test
//    fun `Given a command to end player turn that is already ended, when I execute the command, then I receive an error`() {
//        val command = EndPlayerTurnCmd(gameIdentity = gameIdentity, playerIdentity = playerIdentity1)
//        assert(commandHandler.handle(command) is Valid)
//        assert(commandHandler.handle(command) is Invalid)
//    }
//
//    @Test
//    fun `Given a command to execute on a burraco game that doesn't exist, when I execute the command, then I receive an error`() {
//        val command = EndPlayerTurnCmd(gameIdentity = GameIdentity.create(), playerIdentity = playerIdentity1)
//        assert(commandHandler.handle(command) is Invalid)
//    }
//
//    val eventStore = EventStoreInMemoryBusAdapter()
//    private val commandHandler = CommandHandler(eventStore,FakeGameEventsBrokerProducer())
//    val deck = BurracoDeck.create()
//    val gameIdentity: GameIdentity = GameIdentity.create()
//    val aggregate = BurracoGame(identity = gameIdentity)
//    val playerIdentity1 = PlayerIdentity.create()
//    val playerIdentity2 = PlayerIdentity.create()
//
//    val allCards = ListCardsBuilder.allRanksWithJollyCards()
//        .plus(ListCardsBuilder.allRanksWithJollyCards())
//        .shuffled()
//    val cardsPlayer1 = Pair(playerIdentity1, allCards.take(11))
//    val cardsPlayer2 = Pair(playerIdentity2, allCards.take(11))
//
//    val mazzettoDeck1Cards = allCards.take(11)
//    val mazzettoDeck2Cards = allCards.take(11)
//
//    val discardPileCards = allCards.take(1)
//
//    val burracoDeckCards = allCards.take(108 - 1 - 11 - 11 - 11 - 11)
//
//    val playersCards = mapOf<PlayerIdentity, List<Card>>(cardsPlayer1, cardsPlayer2)
//
//    val events = listOf<Event>(
//        BurracoGameCreated(identity = gameIdentity),
//        PlayerAdded(identity = gameIdentity, playerIdentity = playerIdentity1),
//        PlayerAdded(identity = gameIdentity, playerIdentity = playerIdentity2),
//        GameInitialised(identity = gameIdentity, players = listOf(playerIdentity1,playerIdentity2)),
//
//        CardPickedFromDeck(identity = gameIdentity, playerIdentity = playerIdentity1, card = burracoDeckCards[0]),
//        CardDroppedIntoDiscardPile(
//            identity = gameIdentity,
//            playerIdentity = playerIdentity1,
//            card = burracoDeckCards[0]
//        )
//    )
}