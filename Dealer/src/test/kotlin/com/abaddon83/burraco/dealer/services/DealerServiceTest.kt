package com.abaddon83.burraco.dealer.services

import com.abaddon83.burraco.dealer.commands.AggregateDealerCommandHandler
import com.abaddon83.burraco.dealer.events.*
import com.abaddon83.burraco.dealer.models.Dealer
import com.abaddon83.burraco.dealer.models.DealerIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.dealer.adapters.commandController.CommandControllerAdapter
import com.abaddon83.burraco.testHelpers.DummyExternalEventPublisherAdapter
import io.github.abaddon.kcqrs.core.IIdentity
import io.github.abaddon.kcqrs.core.persistence.InMemoryEventStoreRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal class DealerServiceTest{
    private val log: Logger = LoggerFactory.getLogger(this::class.simpleName)
    private fun emptyAggregate(): (identity: IIdentity) -> Dealer = { Dealer.empty() }
    private val inMemoryRepository = InMemoryEventStoreRepository<Dealer>("stream1",emptyAggregate())
    private val commandControllerAdapter= CommandControllerAdapter(AggregateDealerCommandHandler(inMemoryRepository,DummyExternalEventPublisherAdapter()))
    private val dealerService = DealerService(commandControllerAdapter)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given 1 player when create a dealer then exception`() = runTest {

        val dealerIdentity = DealerIdentity.create()
        val gameIdentity= GameIdentity.create()
        val players = listOf(
            PlayerIdentity.create()
        )
        when(val resul=dealerService.dealCards(dealerIdentity, gameIdentity, players)){
            is DealerServiceResult.Invalid -> {
                log.error(resul.err.toMap().toString(),resul.err)
                assert(true)
            }
            is DealerServiceResult.Valid -> assert(false)

        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given 2 players when create a dealer then all cards are dealt`() = runTest {

        val dealerIdentity = DealerIdentity.create()
        val gameIdentity= GameIdentity.create()
        val players = listOf(
            PlayerIdentity.create(),
            PlayerIdentity.create()
        )
        when(val resul=dealerService.dealCards(dealerIdentity, gameIdentity, players)){
            is DealerServiceResult.Invalid -> {
                log.error(resul.err.toMap().toString(),resul.err)
                assert(false)
            }
            is DealerServiceResult.Valid -> {
                log.debug(resul.value.dealer.cardsAvailable.size.toString())
                val events=inMemoryRepository.loadEventsFromStorage(resul.value.dealer.id)
                events.forEach{event->
                    when(event){
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given 3 players when create a dealer then all cards are dealt`() = runTest {

        val dealerIdentity = DealerIdentity.create()
        val gameIdentity= GameIdentity.create()
        val players = listOf(
            PlayerIdentity.create(),
            PlayerIdentity.create(),
            PlayerIdentity.create()
        )
        when(val resul=dealerService.dealCards(dealerIdentity, gameIdentity, players)){
            is DealerServiceResult.Invalid -> {
                log.error(resul.err.toMap().toString(),resul.err)
                assert(false)
            }
            is DealerServiceResult.Valid -> {
                log.debug(resul.value.dealer.cardsAvailable.size.toString())
                val events=inMemoryRepository.loadEventsFromStorage(resul.value.dealer.id)
                events.forEach{event->
                    when(event){
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given 4 players when create a dealer then all cards are dealt`() = runTest {

        val dealerIdentity = DealerIdentity.create()
        val gameIdentity= GameIdentity.create()
        val players = listOf(
            PlayerIdentity.create(),
            PlayerIdentity.create(),
            PlayerIdentity.create(),
            PlayerIdentity.create()
        )
        when(val resul=dealerService.dealCards(dealerIdentity, gameIdentity, players)){
            is DealerServiceResult.Invalid -> {
                log.error(resul.err.toMap().toString(),resul.err)
                assert(false)
            }
            is DealerServiceResult.Valid -> {
                log.debug(resul.value.dealer.cardsAvailable.size.toString())
                val events=inMemoryRepository.loadEventsFromStorage(resul.value.dealer.id)
                events.forEach{event->
                    when(event){
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