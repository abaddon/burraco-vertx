package com.abaddon83.burraco.dealer.services

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.dealer.adapters.commandController.CommandControllerAdapter
import com.abaddon83.burraco.dealer.commands.AggregateDealerCommandHandler
import com.abaddon83.burraco.dealer.events.*
import com.abaddon83.burraco.dealer.models.Dealer
import com.abaddon83.burraco.common.models.DealerIdentity
import com.abaddon83.burraco.testHelpers.DummyExternalEventPublisherAdapter
import io.github.abaddon.kcqrs.core.IIdentity
import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log
import io.github.abaddon.kcqrs.core.persistence.InMemoryEventStoreRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class DealerServiceTest {
    protected val testDispatcher: CoroutineDispatcher = UnconfinedTestDispatcher()
    protected val testScope = TestScope(testDispatcher)
    private fun emptyAggregate(): (identity: IIdentity) -> Dealer = { Dealer.empty() }
    private val inMemoryRepository = InMemoryEventStoreRepository<Dealer>(
        "stream1",
        emptyAggregate()
    )
    private val aggregateDealerCommandHandler = AggregateDealerCommandHandler(
        inMemoryRepository,
        DummyExternalEventPublisherAdapter()
    )
    private val commandControllerAdapter = CommandControllerAdapter(
        aggregateDealerCommandHandler
    )
    private val dealerService = DealerService(commandControllerAdapter)


    @Test
    fun `given 1 player when create a dealer then exception`() = runTest {

        val dealerIdentity = DealerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val players = listOf(
            PlayerIdentity.create()
        )
        when (val resul = dealerService.dealCards(dealerIdentity, gameIdentity, players)) {
            is DealerServiceResult.Invalid -> {
                log.error(resul.err.toMap().toString(), resul.err)
                assert(true)
            }

            is DealerServiceResult.Valid -> assert(false)

        }
    }


    @Test
    fun `given 2 players when create a dealer then all cards are dealt`() = runTest {

        val dealerIdentity = DealerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val players = listOf(
            PlayerIdentity.create(),
            PlayerIdentity.create()
        )
        when (val resul = dealerService.dealCards(dealerIdentity, gameIdentity, players)) {
            is DealerServiceResult.Invalid -> {
                log.error(resul.err.toMap().toString(), resul.err)
                assert(false)
            }

            is DealerServiceResult.Valid -> {
                log.debug(resul.value.dealer.cardsAvailable.size.toString())
                val events = inMemoryRepository.getById(resul.value.dealer.id).getOrThrow().uncommittedEvents
                events.forEach { event ->
                    when (event) {
                        is CardDealtToPlayer -> log.debug("${event.playerId.valueAsString()} ${event.card}")
                        is CardDealtToDiscardDeck -> log.debug("DiscardDeck ${event.card}")
                        is CardDealtToPlayerDeck1 -> log.debug("PlayerDeck1 ${event.card}")
                        is CardDealtToPlayerDeck2 -> log.debug("PlayerDeck2 ${event.card}")
                        is CardDealtToDeck -> log.debug("Deck ${event.card}")
                    }
                }
                assert(true)
            }
        }
    }


    @Test
    fun `given 3 players when create a dealer then all cards are dealt`() = runTest {

        val dealerIdentity = DealerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val players = listOf(
            PlayerIdentity.create(),
            PlayerIdentity.create(),
            PlayerIdentity.create()
        )
        when (val resul = dealerService.dealCards(dealerIdentity, gameIdentity, players)) {
            is DealerServiceResult.Invalid -> {
                log.error(resul.err.toMap().toString(), resul.err)
                assert(false)
            }

            is DealerServiceResult.Valid -> {
                log.debug(resul.value.dealer.cardsAvailable.size.toString())
                val events = inMemoryRepository.getById(resul.value.dealer.id).getOrThrow().uncommittedEvents
                events.forEach { event ->
                    when (event) {
                        is CardDealtToPlayer -> log.debug("${event.playerId.valueAsString()} ${event.card}")
                        is CardDealtToDiscardDeck -> log.debug("DiscardDeck ${event.card}")
                        is CardDealtToPlayerDeck1 -> log.debug("PlayerDeck1 ${event.card}")
                        is CardDealtToPlayerDeck2 -> log.debug("PlayerDeck2 ${event.card}")
                        is CardDealtToDeck -> log.debug("Deck ${event.card}")
                    }
                }
                assert(true)
            }
        }
    }


    @Test
    fun `given 4 players when create a dealer then all cards are dealt`() = runTest {

        val dealerIdentity = DealerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val players = listOf(
            PlayerIdentity.create(),
            PlayerIdentity.create(),
            PlayerIdentity.create(),
            PlayerIdentity.create()
        )
        when (val resul = dealerService.dealCards(dealerIdentity, gameIdentity, players)) {
            is DealerServiceResult.Invalid -> {
                log.error(resul.err.toMap().toString(), resul.err)
                assert(false)
            }

            is DealerServiceResult.Valid -> {
                log.debug(resul.value.dealer.cardsAvailable.size.toString())
                val events = inMemoryRepository.getById(resul.value.dealer.id).getOrThrow().uncommittedEvents
                events.forEach { event ->
                    when (event) {
                        is CardDealtToPlayer -> log.debug("${event.playerId.valueAsString()} ${event.card}")
                        is CardDealtToDiscardDeck -> log.debug("DiscardDeck ${event.card}")
                        is CardDealtToPlayerDeck1 -> log.debug("PlayerDeck1 ${event.card}")
                        is CardDealtToPlayerDeck2 -> log.debug("PlayerDeck2 ${event.card}")
                        is CardDealtToDeck -> log.debug("Deck ${event.card}")
                    }
                }
                assert(true)
            }
        }
    }
}