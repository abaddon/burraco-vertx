package com.abaddon83.burraco.player.adapter.commandController

import com.abaddon83.burraco.common.helpers.Validated
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.player.DomainError
import com.abaddon83.burraco.player.DomainResult
import com.abaddon83.burraco.player.command.playerDraft.CreatePlayer
import com.abaddon83.burraco.player.event.PlayerCreated
import com.abaddon83.burraco.player.model.player.Player
import com.abaddon83.burraco.player.model.player.PlayerDraft
import io.github.abaddon.kcqrs.core.IIdentity
import io.github.abaddon.kcqrs.core.domain.IAggregateCommandHandler
import io.github.abaddon.kcqrs.core.domain.SimpleAggregateCommandHandler
import io.github.abaddon.kcqrs.core.domain.messages.commands.ICommand
import io.github.abaddon.kcqrs.core.persistence.InMemoryEventStoreRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CommandControllerAdapterTest {

    private lateinit var commandControllerAdapter: CommandControllerAdapter
    private val streamName = "PlayerTest"

    @BeforeEach
    fun setUp() {
        fun emptyAggregate(): (identity: IIdentity) -> PlayerDraft = { PlayerDraft.empty() }
        val commandHandler = SimpleAggregateCommandHandler<Player>(
            InMemoryEventStoreRepository<Player>(streamName, emptyAggregate())
        )
        commandControllerAdapter = CommandControllerAdapter(commandHandler)
    }

    @Test
    fun `given valid gameIdentity and user when createPlayer then returns Valid DomainResult`() = runBlocking {
        // Given
        val gameIdentity = GameIdentity.create()
        val user = "testUser"

        // When
        val result = commandControllerAdapter.createPlayer(gameIdentity, user)

        // Then
        assertTrue(result is Validated.Valid)
        val domainResult = (result as Validated.Valid).value
        assertTrue(domainResult.events.any { it is PlayerCreated })
        
        val playerCreatedEvent = domainResult.events.first() as PlayerCreated
        assertEquals(gameIdentity, playerCreatedEvent.gameIdentity)
        assertEquals(user, playerCreatedEvent.user)
        
        assertTrue(domainResult.player is PlayerDraft)
        val playerDraft = domainResult.player as PlayerDraft
        assertEquals(gameIdentity, playerDraft.gameIdentity)
        assertEquals(user, playerDraft.user)
    }

    @Test
    fun `given empty user when createPlayer then returns Valid DomainResult with empty user`() = runBlocking {
        // Given
        val gameIdentity = GameIdentity.create()
        val user = ""

        // When
        val result = commandControllerAdapter.createPlayer(gameIdentity, user)

        // Then
        assertTrue(result is Validated.Valid)
        val domainResult = (result as Validated.Valid).value
        
        val playerCreatedEvent = domainResult.events.first() as PlayerCreated
        assertEquals("", playerCreatedEvent.user)
        
        val playerDraft = domainResult.player as PlayerDraft
        assertEquals("", playerDraft.user)
    }

    @Test
    fun `given multiple createPlayer calls when createPlayer then each gets unique PlayerIdentity`() = runBlocking {
        // Given
        val gameIdentity = GameIdentity.create()
        val user = "testUser"

        // When
        val result1 = commandControllerAdapter.createPlayer(gameIdentity, user)
        val result2 = commandControllerAdapter.createPlayer(gameIdentity, user)

        // Then
        assertTrue(result1 is Validated.Valid)
        assertTrue(result2 is Validated.Valid)
        
        val domainResult1 = (result1 as Validated.Valid).value
        val domainResult2 = (result2 as Validated.Valid).value
        
        val playerCreatedEvent1 = domainResult1.events.first() as PlayerCreated
        val playerCreatedEvent2 = domainResult2.events.first() as PlayerCreated
        
        assertNotEquals(playerCreatedEvent1.aggregateId, playerCreatedEvent2.aggregateId)
    }

    @Test
    fun `given different gameIdentities when createPlayer then creates players for different games`() = runBlocking {
        // Given
        val gameIdentity1 = GameIdentity.create()
        val gameIdentity2 = GameIdentity.create()
        val user = "testUser"

        // When
        val result1 = commandControllerAdapter.createPlayer(gameIdentity1, user)
        val result2 = commandControllerAdapter.createPlayer(gameIdentity2, user)

        // Then
        assertTrue(result1 is Validated.Valid)
        assertTrue(result2 is Validated.Valid)
        
        val domainResult1 = (result1 as Validated.Valid).value
        val domainResult2 = (result2 as Validated.Valid).value
        
        val playerCreatedEvent1 = domainResult1.events.first() as PlayerCreated
        val playerCreatedEvent2 = domainResult2.events.first() as PlayerCreated
        
        assertEquals(gameIdentity1, playerCreatedEvent1.gameIdentity)
        assertEquals(gameIdentity2, playerCreatedEvent2.gameIdentity)
        assertEquals(user, playerCreatedEvent1.user)
        assertEquals(user, playerCreatedEvent2.user)
    }
}