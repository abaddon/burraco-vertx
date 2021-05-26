package com.abaddon83.vertx.burraco.game.commands

import com.abaddon83.utils.ddd.Event
import com.abaddon83.vertx.burraco.game.adapters.eventStoreAdapter.inMemory.EventStoreInMemoryAdapter
import com.abaddon83.burraco.common.events.BurracoGameCreated
import com.abaddon83.burraco.common.events.GameInitialised
import com.abaddon83.burraco.common.events.PlayerAdded
import com.abaddon83.vertx.burraco.game.models.BurracoDeck
import com.abaddon83.vertx.burraco.game.models.BurracoGame
import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.vertx.burraco.game.models.decks.ListCardsBuilder
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.utils.functionals.Invalid
import com.abaddon83.utils.functionals.Valid
import com.abaddon83.vertx.burraco.game.adapters.eventBrokerProducer.FakeEventBrokerProducer
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class PickUpCardsFromDiscardPileCmdTest {

    @BeforeAll
    fun loadEvents(){
        eventStore.save(events)
    }

    @Test
    fun `Given a command to pick up a card from the deck, when I execute the command, then the card is picked up`(){
        val command = PickUpCardsFromDiscardPileCmd(gameIdentity = gameIdentity,playerIdentity = playerIdentity1)
        assert(commandHandler.handle(command) is Valid)
    }

    @Test
    fun `Given a player with a card already picked-up, when I receive the comand to pick up a card, then nothing happened`(){
        val command = PickUpCardsFromDiscardPileCmd(gameIdentity = gameIdentity,playerIdentity = playerIdentity1)
        assert(commandHandler.handle(command) is Valid)
        assert(commandHandler.handle(command) is Invalid)
    }

    @Test
    fun `Given a command to execute on a burraco game that doesn't exist, when I execute the command, then I receive an error`(){
        val command = PickUpCardsFromDiscardPileCmd(gameIdentity = GameIdentity.create(),playerIdentity = playerIdentity1)
        assert(commandHandler.handle(command) is Invalid)
    }

    val eventStore = EventStoreInMemoryAdapter()
    private val commandHandler = CommandHandler(eventStore, FakeEventBrokerProducer())
    val deck = BurracoDeck.create()
    val gameIdentity: GameIdentity = GameIdentity.create()
    val aggregate = BurracoGame(identity = gameIdentity)
    val playerIdentity1 = PlayerIdentity.create()
    val playerIdentity2 = PlayerIdentity.create()

    val allCards = ListCardsBuilder.allRanksWithJollyCards()
            .plus(ListCardsBuilder.allRanksWithJollyCards())
            .shuffled()
    val cardsPlayer1 = Pair(playerIdentity1,allCards.take(11))
    val cardsPlayer2 = Pair(playerIdentity2,allCards.take(11))

    val mazzettoDeck1Cards = allCards.take(11)
    val mazzettoDeck2Cards = allCards.take(11)

    val discardPileCards = allCards.take(1)

    val burracoDeckCards = allCards.take(108-1-11-11-11-11)

    val playersCards = mapOf<PlayerIdentity,List<Card>>(cardsPlayer1,cardsPlayer2)

    val events = listOf<Event>(
            BurracoGameCreated(identity = gameIdentity),
            PlayerAdded(identity = gameIdentity, playerIdentity = playerIdentity1),
            PlayerAdded(identity = gameIdentity, playerIdentity = playerIdentity2),
//            CardsDealtToPlayer(identity = gameIdentity,player = playerIdentity1,cards = playersCards[playerIdentity1] ?: error("playerIdentity1 not found")),
//            CardsDealtToPlayer(identity = gameIdentity,player = playerIdentity2,cards = playersCards[playerIdentity2] ?: error("playerIdentity2 not found")),
            GameInitialised(identity = gameIdentity, players = listOf(playerIdentity1,playerIdentity2)),
    )
}