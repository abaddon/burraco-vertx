package com.abaddon83.burraco.dealer.services

import com.abaddon83.burraco.dealer.events.*
import com.abaddon83.burraco.dealer.helpers.Validated
import com.abaddon83.burraco.dealer.models.Dealer
import com.abaddon83.burraco.dealer.models.DealerIdentity
import com.abaddon83.burraco.dealer.models.GameIdentity
import com.abaddon83.burraco.dealer.models.PlayerIdentity
import io.github.abaddon.kcqrs.core.IIdentity
import io.github.abaddon.kcqrs.core.domain.SimpleAggregateCommandHandler
import io.github.abaddon.kcqrs.core.persistence.InMemoryEventStoreRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class DealerServiceTest{
    private fun emptyAggregate(): (identity: IIdentity) -> Dealer = { Dealer.empty() }
    private val inMemoryRepository = InMemoryEventStoreRepository<Dealer>("stream1",emptyAggregate())


    @Test
    fun `given 1 player when create a dealer then exception`() = runTest {

        val dealerService = DealerService(SimpleAggregateCommandHandler<Dealer>(inMemoryRepository))
        val dealerIdentity = DealerIdentity.create()
        val gameIdentity= GameIdentity.create()
        val players = listOf(
            PlayerIdentity.create()
        )
        when(val resul=dealerService.dealCards(dealerIdentity, gameIdentity, players)){
            is Validated.Invalid -> {
                println(resul.err.toMap())
                assert(true)
            }
            is Validated.Valid -> assert(false)

        }
    }

    @Test
    fun `given 2 players when create a dealer then all cards are dealt`() = runTest {

        val dealerService = DealerService(SimpleAggregateCommandHandler<Dealer>(inMemoryRepository))
        val dealerIdentity = DealerIdentity.create()
        val gameIdentity= GameIdentity.create()
        val players = listOf(
            PlayerIdentity.create(),
            PlayerIdentity.create(),
            //PlayerIdentity.create(),
            //PlayerIdentity.create()
        )
        when(val resul=dealerService.dealCards(dealerIdentity, gameIdentity, players)){
            is Validated.Invalid -> {
                println(resul.err.toMap())
                assert(false)
            }
            is Validated.Valid -> {
                println(resul.value.dealer.cardsAvailable.size)
                val events=inMemoryRepository.loadEventsFromStorage(resul.value.dealer.id)
                events.forEach{events->
                    when(events){
                        is CardDealtToPlayer -> println("${events.playerId.valueAsString()} ${events.card}")
                        is CardDealtToDiscardDeck -> println("DiscardDeck ${events.card}")
                        is CardDealtToPlayerDeck1 -> println("PlayerDeck1 ${events.card}")
                        is CardDealtToPlayerDeck2 -> println("PlayerDeck2 ${events.card}")
                        is CardDealtToDeck -> println("Deck ${events.card}")
                    }
                }
                assert(true)
            }
        }
    }

    @Test
    fun `given 3 players when create a dealer then all cards are dealt`() = runTest {

        val dealerService = DealerService(SimpleAggregateCommandHandler<Dealer>(inMemoryRepository))
        val dealerIdentity = DealerIdentity.create()
        val gameIdentity= GameIdentity.create()
        val players = listOf(
            PlayerIdentity.create(),
            PlayerIdentity.create(),
            PlayerIdentity.create(),
            //PlayerIdentity.create()
        )
        when(val resul=dealerService.dealCards(dealerIdentity, gameIdentity, players)){
            is Validated.Invalid -> {
                println(resul.err.toMap())
                assert(false)
            }
            is Validated.Valid -> {
                println(resul.value.dealer.cardsAvailable.size)
                val events=inMemoryRepository.loadEventsFromStorage(resul.value.dealer.id)
                events.forEach{events->
                    when(events){
                        is CardDealtToPlayer -> println("${events.playerId.valueAsString()} ${events.card}")
                        is CardDealtToDiscardDeck -> println("DiscardDeck ${events.card}")
                        is CardDealtToPlayerDeck1 -> println("PlayerDeck1 ${events.card}")
                        is CardDealtToPlayerDeck2 -> println("PlayerDeck2 ${events.card}")
                        is CardDealtToDeck -> println("Deck ${events.card}")
                    }
                }
                assert(true)
            }
        }
    }

    @Test
    fun `given 4 players when create a dealer then all cards are dealt`() = runTest {

        val dealerService = DealerService(SimpleAggregateCommandHandler<Dealer>(inMemoryRepository))
        val dealerIdentity = DealerIdentity.create()
        val gameIdentity= GameIdentity.create()
        val players = listOf(
            PlayerIdentity.create(),
            PlayerIdentity.create(),
            PlayerIdentity.create(),
            //PlayerIdentity.create()
        )
        when(val resul=dealerService.dealCards(dealerIdentity, gameIdentity, players)){
            is Validated.Invalid -> {
                println(resul.err.toMap())
                assert(false)
            }
            is Validated.Valid -> {
                println(resul.value.dealer.cardsAvailable.size)
                val events=inMemoryRepository.loadEventsFromStorage(resul.value.dealer.id)
                events.forEach{events->
                    when(events){
                        is CardDealtToPlayer -> println("${events.playerId.valueAsString()} ${events.card}")
                        is CardDealtToDiscardDeck -> println("DiscardDeck ${events.card}")
                        is CardDealtToPlayerDeck1 -> println("PlayerDeck1 ${events.card}")
                        is CardDealtToPlayerDeck2 -> println("PlayerDeck2 ${events.card}")
                        is CardDealtToDeck -> println("Deck ${events.card}")
                    }
                }
                assert(true)
            }
        }
    }
}