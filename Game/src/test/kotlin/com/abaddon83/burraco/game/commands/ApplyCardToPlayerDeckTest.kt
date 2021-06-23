package com.abaddon83.burraco.game.commands

import com.abaddon83.burraco.common.events.BurracoGameCreated
import com.abaddon83.burraco.common.events.GameInitialised
import com.abaddon83.burraco.common.events.PlayerAdded
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.burraco.common.models.valueObjects.Ranks
import com.abaddon83.burraco.common.models.valueObjects.Suits
import com.abaddon83.utils.ddd.Event
import com.abaddon83.utils.functionals.Valid
import com.abaddon83.burraco.game.adapters.eventBrokerProducer.FakeGameEventsBrokerProducer
import com.abaddon83.burraco.game.adapters.eventStoreAdapter.inMemory.EventStoreInMemoryBusAdapter
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

internal class ApplyCardToPlayerDeckTest{
    companion object {
        private val eventStore = EventStoreInMemoryBusAdapter()
        private val commandHandler = CommandHandler(eventStore, FakeGameEventsBrokerProducer())
        private val gameIdentity: GameIdentity = GameIdentity.create()
        val playerIdentity1 = PlayerIdentity.create()
        val playerIdentity2 = PlayerIdentity.create()
        val events = listOf<Event>(
            BurracoGameCreated(identity = gameIdentity),
            PlayerAdded(identity = gameIdentity, playerIdentity = playerIdentity1),
            PlayerAdded(identity = gameIdentity, playerIdentity = playerIdentity2),
            GameInitialised(identity = gameIdentity,players = listOf(playerIdentity1, playerIdentity2))
        )

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            eventStore.save(events) {}
        }
    }

    @Test
    fun `Given a game initialised, when I execute the command ApplyCardToPlayerDeck, then the card is assigned to the player deck`(){
        val command = ApplyCardToPlayerDeck(gameIdentity = gameIdentity, card = Card(Suits.Heart, Ranks.Ace), playerDeckId = 0)
        commandHandler.handle(command).future()
            .onSuccess { assert(it is Valid) }
            .onFailure { assert(false) }
    }

}