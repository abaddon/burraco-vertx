package com.abaddon83.vertx.burraco.engine.commands

import com.abaddon83.utils.ddd.Event
import com.abaddon83.burraco.common.events.BurracoGameCreated
import com.abaddon83.vertx.burraco.engine.models.BurracoDeck
import com.abaddon83.vertx.burraco.engine.models.BurracoGame
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.utils.functionals.Invalid
import com.abaddon83.utils.functionals.Valid
import com.abaddon83.vertx.burraco.engine.adapters.eventStoreAdapter.inMemory.EventStoreInMemoryAdapter
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

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
    fun `Given a command to add 2 time the same player to the game, when I execute the second command, then I receive an error`(){
        val playerIdentity = PlayerIdentity.create();
        val command = AddPlayerCmd(gameIdentity = gameIdentity, playerIdentityToAdd = playerIdentity)
        assert(commandHandler.handle(command) is Valid)
        val command2 = AddPlayerCmd(gameIdentity = gameIdentity, playerIdentityToAdd = playerIdentity)
        val cmdResult: CmdResult=commandHandler.handle(command2)
        assert(cmdResult is Invalid)
        assertEquals("The player PlayerIdentity(id=${playerIdentity.convertTo().toString()}) is already a player of game GameIdentity(id=${gameIdentity.convertTo().toString()})",(cmdResult as Invalid).err.msg)
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