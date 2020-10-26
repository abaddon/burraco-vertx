package com.abaddon83.vertx.burraco.engine.commands

import com.abaddon83.utils.es.Event
import com.abaddon83.vertx.burraco.engine.events.BurracoGameCreated
import com.abaddon83.vertx.burraco.engine.models.BurracoDeck
import com.abaddon83.vertx.burraco.engine.models.BurracoGame
import com.abaddon83.vertx.burraco.engine.models.games.GameIdentity
import com.abaddon83.vertx.burraco.engine.models.players.PlayerIdentity
import com.abaddon83.utils.functionals.Invalid
import com.abaddon83.utils.functionals.Valid
import com.abaddon83.vertx.burraco.engine.adapters.eventStoreAdapter.inMemory.EventStoreInMemoryAdapter
import org.junit.Before
import org.junit.Test

class AddPlayerCmdTest {

    @Before
    fun loadEvents(){
        eventStore.save(events)
    }

    @Test
    fun `Given a command to add a player to the game, when I execute the command, then the player is added`(){
        val command = AddPlayerCmd(gameIdentity = gameIdentity, playerIdentityToAdd = PlayerIdentity.create())
        assert(commandHandler.handle(command) is Valid)
        val command2 = AddPlayerCmd(gameIdentity = gameIdentity, playerIdentityToAdd = PlayerIdentity.create())
        assert(commandHandler.handle(command2) is Valid)
    }

    @Test
    fun `Given a command to execute on a burraco game that doesn't exist, when I execute the command, then I receive an error`(){
        val command = AddPlayerCmd(gameIdentity = GameIdentity.create(), playerIdentityToAdd = PlayerIdentity.create())
        assert(commandHandler.handle(command) is Invalid)
    }

    val eventStore = EventStoreInMemoryAdapter()
    val gameIdentity: GameIdentity = GameIdentity.create()
    val aggregate = BurracoGame(identity = gameIdentity)
    val deck = BurracoDeck.create()
    val events = listOf<Event>(
            BurracoGameCreated(identity = gameIdentity, deck = deck.cards)
    )
    private val commandHandler = CommandHandler(eventStore)

}