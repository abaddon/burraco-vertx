package com.abaddon83.burraco.dealer.adapters.commandController.kafka.handlers

import com.abaddon83.burraco.common.adapter.kafka.KafkaEvent
import com.abaddon83.burraco.dealer.models.Dealer
import com.abaddon83.burraco.dealer.models.DealerIdentity
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.dealer.ports.CommandControllerPort
import com.abaddon83.burraco.dealer.ports.CommandPortResult
import com.abaddon83.burraco.dealer.ports.DomainError
import com.abaddon83.burraco.dealer.ports.DomainResult
import com.abaddon83.burraco.dealer.services.DealerService
import io.github.abaddon.kcqrs.core.domain.SimpleAggregateCommandHandler
import io.github.abaddon.kcqrs.core.persistence.InMemoryEventStoreRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class CardDealingRequestedHandlerTest {

    private var dummyCommandController = DummyCommandController(
        DealerService(
            SimpleAggregateCommandHandler(InMemoryEventStoreRepository("stream_name", { Dealer.empty() }))
        )
    )
    private val cardsRequestedToDealerHandler = CardsRequestedToDealerHandler(dummyCommandController)

    @Test
    fun `Given a KafkaEvent containing the externalEvent CardDealingRequested, when processed, then command createDeck executed `() {

        val event = KafkaEvent.from("{\"eventName\":\"CardsRequestedToDealer\",\"eventPayload\":\"{\\\"aggregateIdentity\\\":{\\\"identity\\\":\\\"b057aa9f-8659-4a1e-904e-3e61cd8ab957\\\"},\\\"players\\\":[{\\\"identity\\\":\\\"96fdd0a5-311a-4962-a1e3-8b62376d7ca7\\\"},{\\\"identity\\\":\\\"ce43579c-68d4-418e-8179-f43c2bd4fded\\\"}],\\\"eventOwner\\\":\\\"Game\\\",\\\"eventName\\\":\\\"CardsRequestedToDealer\\\"}\"}")
        runBlocking {
            cardsRequestedToDealerHandler.asyncHandle(event)
        }
        assertTrue(dummyCommandController.executed)

    }

    internal class DummyCommandController(override val dealerService: DealerService) : CommandControllerPort {
        var executed: Boolean = false;

        override suspend fun createDeck(
            dealerIdentity: DealerIdentity,
            gameIdentity: GameIdentity,
            players: List<PlayerIdentity>
        ): CommandPortResult<DomainError, DomainResult> {
            executed = true
            return CommandPortResult.Valid(DomainResult(listOf(), Dealer.empty()))
        }

    }
}