package com.abaddon83.burraco.game.commands

import com.abaddon83.utils.ddd.Event
import com.abaddon83.burraco.game.adapters.eventStoreAdapter.inMemory.EventStoreInMemoryBusAdapter
import com.abaddon83.burraco.common.events.BurracoGameCreated
import com.abaddon83.burraco.common.events.PlayerAdded
import com.abaddon83.burraco.game.models.BurracoDeck
import com.abaddon83.burraco.game.models.BurracoGame
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.utils.functionals.Invalid
import com.abaddon83.utils.functionals.Valid
import com.abaddon83.burraco.game.adapters.eventBrokerProducer.FakeGameEventsBrokerProducer
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class InitGameCmdTest {

    companion object {
        private val eventStore = EventStoreInMemoryBusAdapter()
        private val commandHandler = CommandHandler(eventStore, FakeGameEventsBrokerProducer())
        val gameIdentity: GameIdentity = GameIdentity.create()
        val playerIdentity1 = PlayerIdentity.create()
        val playerIdentity2 = PlayerIdentity.create()
        val events = listOf<Event>(
            BurracoGameCreated(identity = gameIdentity),
            PlayerAdded(identity = gameIdentity, playerIdentity = playerIdentity1),
            PlayerAdded(identity = gameIdentity, playerIdentity = playerIdentity2)
        )

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            eventStore.save(events) {}
        }
    }

    @Test
    fun `Given a command to start a game, when I execute the command, then the game is started`(){
        val command = InitGameCmd(gameIdentity = gameIdentity)
        commandHandler.handle(command).future()
            .onSuccess { assert(it is Valid) }
            .onFailure { assert(false) }
    }

    @Test
    fun `Given a burraco game started, when I start the game again, then I receive an error`(){
        val command = InitGameCmd(gameIdentity = gameIdentity)
        commandHandler.handle(command).future()
            .onSuccess { assert(it is Invalid) }
            .onFailure { assert(false) }
    }

    @Test
    fun `Given a command to execute on a burraco game that doesn't exist, when I execute the command, then I receive an error`(){
        val command = InitGameCmd(gameIdentity = GameIdentity.create())
        commandHandler.handle(command).future()
            .onSuccess { assert(it is Invalid) }
            .onFailure { assert(false) }
    }




}