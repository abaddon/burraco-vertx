package com.abaddon83.burraco.dealer.adapters.commandController.kafka.handlers

import com.abaddon83.burraco.common.adapter.kafka.KafkaEvent
import com.abaddon83.burraco.dealer.adapters.commandController.CommandControllerAdapter
import com.abaddon83.burraco.dealer.commands.AggregateDealerCommandHandler
import com.abaddon83.burraco.dealer.models.Dealer
import com.abaddon83.burraco.testHelpers.DummyExternalEventPublisherAdapter
import io.github.abaddon.kcqrs.core.persistence.InMemoryEventStoreRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

internal class CardDealingRequestedHandlerTest {

    private val inMemoryEventStoreRepository = InMemoryEventStoreRepository("stream",{Dealer.empty()})
    private val commandHandler = AggregateDealerCommandHandler(inMemoryEventStoreRepository, DummyExternalEventPublisherAdapter())
    private val cardsRequestedToDealerHandler = CardsRequestedToDealerHandler(CommandControllerAdapter(commandHandler))

    @Test
    fun `Given a KafkaEvent containing the externalEvent CardDealingRequested, when processed, then command createDeck executed `() {

        val event = KafkaEvent.from("{\"eventName\":\"CardsRequestedToDealer\",\"eventPayload\":\"{\\\"aggregateIdentity\\\":{\\\"identity\\\":\\\"b057aa9f-8659-4a1e-904e-3e61cd8ab957\\\"},\\\"players\\\":[{\\\"identity\\\":\\\"96fdd0a5-311a-4962-a1e3-8b62376d7ca7\\\"},{\\\"identity\\\":\\\"ce43579c-68d4-418e-8179-f43c2bd4fded\\\"}],\\\"eventOwner\\\":\\\"Game\\\",\\\"eventName\\\":\\\"CardsRequestedToDealer\\\"}\"}")
        assertDoesNotThrow {
            runBlocking {
                cardsRequestedToDealerHandler.asyncHandle(event)
            }
        }
    }
}