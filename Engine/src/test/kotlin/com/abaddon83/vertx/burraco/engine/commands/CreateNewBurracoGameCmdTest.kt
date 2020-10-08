package com.abaddon83.vertx.burraco.engine.commands

import com.abaddon83.utils.es.Event
import com.abaddon83.vertx.burraco.engine.adapters.eventStoreInMemories.EventStoreInMemoryAdapter
import com.abaddon83.vertx.burraco.engine.models.games.GameIdentity
import com.abaddon83.utils.functionals.Valid
import org.junit.Before
import org.junit.Test

class CreateNewBurracoGameCmdTest {

    @Before
    fun loadEvents(){
        eventStore.save(events)
    }

    @Test
    fun `Given a command to create a new game, when I execute the command, then a new game is created`(){
        val gameIdentity = GameIdentity.create()
        val command = CreateNewBurracoGameCmd(gameIdentity = gameIdentity)
        assert(commandHandler.handle(command) is Valid)
    }

    val eventStore = EventStoreInMemoryAdapter()
    private val commandHandler = CommandHandler(eventStore)
    val events = listOf<Event>()
}