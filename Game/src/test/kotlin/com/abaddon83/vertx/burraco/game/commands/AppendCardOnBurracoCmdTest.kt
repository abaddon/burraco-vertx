package com.abaddon83.vertx.burraco.game.commands


import com.abaddon83.utils.ddd.Event
import com.abaddon83.burraco.common.events.*
import com.abaddon83.vertx.burraco.game.models.BurracoDeck
import com.abaddon83.vertx.burraco.game.models.BurracoGame
import com.abaddon83.vertx.burraco.game.models.BurracoTris
import com.abaddon83.burraco.common.models.identities.BurracoIdentity
import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.vertx.burraco.game.models.decks.ListCardsBuilder
import com.abaddon83.burraco.common.models.valueObjects.Ranks
import com.abaddon83.burraco.common.models.valueObjects.Suits
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.utils.functionals.Invalid
import com.abaddon83.utils.functionals.Valid
import com.abaddon83.vertx.burraco.game.adapters.eventBrokerProducer.FakeGameEventsBrokerProducer
import com.abaddon83.vertx.burraco.game.adapters.eventStoreAdapter.inMemory.EventStoreInMemoryBusAdapter
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class AppendCardOnBurracoCmdTest {

    @BeforeAll
    fun loadEvents(){
        eventStore.save(events)
    }

    @Test
    fun `Given a command to append a card on a tris that exist, when I execute the command, then the card is added to the tris`() {
        val command = AppendCardOnBurracoCmd(gameIdentity = gameIdentity, playerIdentity = playerIdentity1, burracoIdentity = burracoTris.identity(), cardsToAppend = listOf(cardToAppend))
        assert(commandHandler.handle(command) is Valid)
    }

    @Test
    fun `Given a command to append a card on a tris that doesn't exist, when I execute the command, then I receive an error`() {
        val command = AppendCardOnBurracoCmd(gameIdentity = gameIdentity, playerIdentity = playerIdentity1, burracoIdentity = BurracoIdentity.create(), cardsToAppend = listOf(cardToAppend))
        assert(commandHandler.handle(command) is Invalid)
    }

    @Test
    fun `Given a command to execute on a burraco game that doesn't exist, when I execute the command, then I receive an error`() {
        val command = AppendCardOnBurracoCmd(gameIdentity = GameIdentity.create(), playerIdentity = playerIdentity1, burracoIdentity = burracoTris.identity(), cardsToAppend = listOf(cardToAppend))
        assert(commandHandler.handle(command) is Invalid)
    }


    val eventStore = EventStoreInMemoryBusAdapter()
    private val commandHandler = CommandHandler(eventStore, FakeGameEventsBrokerProducer())
    val deck = BurracoDeck.create()
    val gameIdentity: GameIdentity = GameIdentity.create()
    val aggregate = BurracoGame(identity = gameIdentity)
    val playerIdentity1 = PlayerIdentity.create()
    val playerIdentity2 = PlayerIdentity.create()

    val allCards = ListCardsBuilder.allRanksWithJollyCards()
            .plus(ListCardsBuilder.allRanksWithJollyCards())
            .shuffled()

    val burracoTris = BurracoTris(
            BurracoIdentity.create(),
            Ranks.Five,
            listOf(Card(Suits.Tile, rank = Ranks.Five), Card(Suits.Heart, rank = Ranks.Five), Card(Suits.Tile, rank = Ranks.Five)))

    val cardToAppend = Card(Suits.Clover, rank = Ranks.Five)

    val cardsPlayer1 = Pair(playerIdentity1, allCards.take(7).plus(burracoTris.showCards()).plus(cardToAppend))
    val cardsPlayer2 = Pair(playerIdentity2, allCards.take(11))

    val mazzettoDeck1Cards = allCards.take(11)
    val mazzettoDeck2Cards = allCards.take(11)

    val discardPileCards = allCards.take(1)

    val burracoDeckCards = allCards.take(108 - 1 - 11 - 11 - 11 - 11)

    val playersCards = mapOf<PlayerIdentity, List<Card>>(cardsPlayer1, cardsPlayer2)



    val events = listOf<Event>(
            BurracoGameCreated(identity = gameIdentity),
            PlayerAdded(identity = gameIdentity, playerIdentity = playerIdentity1),
            PlayerAdded(identity = gameIdentity, playerIdentity = playerIdentity2),
            GameInitialised(identity = gameIdentity, players = listOf(playerIdentity1,playerIdentity2)),
            //CardsDealtToPlayer(identity = gameIdentity,player = playerIdentity1,cards = playersCards[playerIdentity1] ?: error("playerIdentity1 not found")),
            //CardsDealtToPlayer(identity = gameIdentity,player = playerIdentity2,cards = playersCards[playerIdentity2] ?: error("playerIdentity2 not found")),
            //GameStarted(identity = gameIdentity),
            //CardPickedFromDeck(identity = gameIdentity, playerIdentity = playerIdentity1, card = burracoDeckCards[0]),
            //TrisDropped(identity = gameIdentity, playerIdentity = playerIdentity1, tris = burracoTris)
    )

}