package com.abaddon83.burraco.game.commands

import com.abaddon83.utils.ddd.Event
import com.abaddon83.burraco.game.adapters.eventStoreAdapter.inMemory.EventStoreInMemoryBusAdapter
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.utils.functionals.Valid
import com.abaddon83.burraco.game.adapters.eventBrokerProducer.FakeGameEventsBrokerProducer
import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test


class CreateNewBurracoGameCmdTest {

    companion object {
        private val eventStore = EventStoreInMemoryBusAdapter()
        private val commandHandler = CommandHandler(eventStore, FakeGameEventsBrokerProducer())
        private val events = listOf<Event>()

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            eventStore.save(events) {}
        }
    }

    @Test
    fun `Given a valid command to create a new game, when I execute the command, then a new game is created`(){
        val gameIdentity = GameIdentity.create()
        val command = CreateNewBurracoGameCmd(gameIdentity = gameIdentity)
        commandHandler.handle(command).future()
            .onSuccess { cmdResult ->
                assert(cmdResult is Valid)
            }
            .onFailure { assert(false) }
    }


}