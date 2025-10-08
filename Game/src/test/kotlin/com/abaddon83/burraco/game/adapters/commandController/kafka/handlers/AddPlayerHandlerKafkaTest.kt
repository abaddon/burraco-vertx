package com.abaddon83.burraco.game.adapters.commandController.kafka.handlers

import com.abaddon83.burraco.common.externalEvents.KafkaEvent
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.game.adapters.commandController.CommandControllerAdapter
import com.abaddon83.burraco.game.commands.AggregateGameCommandHandler
import com.abaddon83.burraco.game.helpers.DummyExternalEventPublisherAdapter
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameDraft
import io.github.abaddon.kcqrs.core.persistence.InMemoryEventStoreRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
internal class AddPlayerHandlerKafkaTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val inMemoryEventStoreRepository: InMemoryEventStoreRepository<Game> = InMemoryEventStoreRepository(
        "stream",
        { GameDraft.empty() }
    )
    private val commandHandler = AggregateGameCommandHandler(
        inMemoryEventStoreRepository,
        DummyExternalEventPublisherAdapter()
    )
    private val addPlayerHandler = AddPlayerHandlerKafka(
        CommandControllerAdapter(commandHandler)
    )

    @Test
    fun `Given a KafkaEvent containing PlayerCreated, when processed, then addPlayer command executed`() {
        val gameIdentity = GameIdentity.create()
        val playerIdentity = PlayerIdentity.create()

        val playerCreatedPayload = """
            {
                "aggregateIdentity": {"identity": "${playerIdentity.valueAsString()}"},
                "gameIdentity": {"identity": "${gameIdentity.valueAsString()}"},
                "eventOwner": "Player",
                "eventName": "PlayerCreated"
            }
        """.trimIndent()

        val event = KafkaEvent("PlayerCreated", playerCreatedPayload)

        assertDoesNotThrow {
            addPlayerHandler.handle(event)
        }
    }

    @Test
    fun `Given a KafkaEvent containing PlayerCreated for non-existent game, when processed, then event is committed`() {
        val gameIdentity = GameIdentity.create()
        val playerIdentity = PlayerIdentity.create()

        val playerCreatedPayload = """
            {
                "aggregateIdentity": {"identity": "${playerIdentity.valueAsString()}"},
                "gameIdentity": {"identity": "${gameIdentity.valueAsString()}"},
                "eventOwner": "Player",
                "eventName": "PlayerCreated"
            }
        """.trimIndent()

        val event = KafkaEvent("PlayerCreated", playerCreatedPayload)

        assertDoesNotThrow {
            addPlayerHandler.handle(event)
        }
    }
}