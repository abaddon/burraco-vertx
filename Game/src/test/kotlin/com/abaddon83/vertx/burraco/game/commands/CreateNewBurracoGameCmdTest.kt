package com.abaddon83.vertx.burraco.game.commands

import com.abaddon83.utils.ddd.Event
import com.abaddon83.vertx.burraco.game.adapters.eventStoreAdapter.inMemory.EventStoreInMemoryBusAdapter
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.utils.functionals.Valid
import com.abaddon83.vertx.burraco.game.adapters.eventBrokerProducer.FakeEventBrokerProducer
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test


class CreateNewBurracoGameCmdTest {

    @BeforeAll
    fun loadEvents(){
        eventStore.save(events)
    }

    @Test
    fun `Given a command to create a new game, when I execute the command, then a new game is created`(){
        val gameIdentity = GameIdentity.create()
        val command = CreateNewBurracoGameCmd(gameIdentity = gameIdentity)
        assert(commandHandler.handle(command) is Valid)
    }

    val eventStore = EventStoreInMemoryBusAdapter()
    private val commandHandler = CommandHandler(eventStore, FakeEventBrokerProducer())
    val events = listOf<Event>()
}