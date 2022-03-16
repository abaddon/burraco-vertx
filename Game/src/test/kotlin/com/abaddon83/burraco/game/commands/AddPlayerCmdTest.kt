package com.abaddon83.burraco.game.commands

import com.abaddon83.utils.ddd.Event
import com.abaddon83.burraco.common.events.BurracoGameCreated
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.utils.functionals.Invalid
import com.abaddon83.utils.functionals.Valid
import com.abaddon83.burraco.game.adapters.eventBrokerProducer.FakeGameEventsBrokerProducer
import com.abaddon83.burraco.game.adapters.eventStoreAdapter.inMemory.EventStoreInMemoryBusAdapter
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class AddPlayerCmdTest {

    companion object {
        private val eventStore = EventStoreInMemoryBusAdapter()
        private val commandHandler = CommandHandler(eventStore, FakeGameEventsBrokerProducer())
        private val gameIdentity: GameIdentity = GameIdentity.create()
        private val events = listOf<Event>(
            BurracoGameCreated(identity = gameIdentity)
        )

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            eventStore.save(events) {}
        }
    }

    @Test
    fun `Given a command to add a player to the game, when I execute the command, then the player is added`(){
        val command = AddPlayerCmd(gameIdentity = gameIdentity, playerIdentityToAdd = PlayerIdentity.create())
        commandHandler.handle(command).future()
            .onSuccess { assert(it is Valid) }
            .onFailure { assert(false) }
    }

    @Test
    fun `Given two commands to add a player to the game, when I execute the commands, then the players are added`(){
        val command = AddPlayerCmd(gameIdentity = gameIdentity, playerIdentityToAdd = PlayerIdentity.create())
        commandHandler.handle(command).future()
            .onSuccess { assert(it is Valid) }
            .onFailure { assert(false) }

        val command2 = AddPlayerCmd(gameIdentity = gameIdentity, playerIdentityToAdd = PlayerIdentity.create())
        commandHandler.handle(command2).future()
            .onSuccess { assert(it is Valid) }
            .onFailure { assert(false) }
    }

    @Test
    fun `Given a command to add 2 time the same player to the game, when I execute the second command, then I receive an error`(){
        val playerIdentity = PlayerIdentity.create();
        val command = AddPlayerCmd(gameIdentity = gameIdentity, playerIdentityToAdd = playerIdentity)

        commandHandler.handle(command).future()
            .onSuccess { assert(it is Valid) }
            .onFailure { assert(false) }

        val command2 = AddPlayerCmd(gameIdentity = gameIdentity, playerIdentityToAdd = playerIdentity)
        commandHandler.handle(command2).future()
            .onSuccess { assert(it is Invalid) }
            .onFailure { assert(false) }
    }

    @Test
    fun `Given a command to execute on a burraco game that doesn't exist, when I execute the command, then I receive an error`(){
        val command = AddPlayerCmd(gameIdentity = GameIdentity.create(), playerIdentityToAdd = PlayerIdentity.create())
        commandHandler.handle(command).future()
            .onSuccess { assert(it is Invalid) }
            .onFailure { assert(false) }
    }

}