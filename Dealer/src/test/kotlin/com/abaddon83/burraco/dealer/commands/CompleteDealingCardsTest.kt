package com.abaddon83.burraco.dealer.commands

import com.abaddon83.burraco.common.models.DealerIdentity
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.event.dealer.CardDealtToDeck
import com.abaddon83.burraco.common.models.event.dealer.DealerEvent
import com.abaddon83.burraco.common.models.event.dealer.DealingCompleted
import com.abaddon83.burraco.common.models.event.dealer.DeckCreated
import com.abaddon83.burraco.dealer.helpers.CardsHelper
import com.abaddon83.burraco.dealer.models.Dealer
import com.abaddon83.burraco.helper.DummyExternalEventPublisherAdapter
import io.github.abaddon.kcqrs.core.IIdentity
import io.github.abaddon.kcqrs.core.domain.IAggregateCommandHandler
import io.github.abaddon.kcqrs.core.domain.messages.commands.ICommand
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.github.abaddon.kcqrs.test.KcqrsAggregateTestSpecification

internal class Given_dealerWithAllCardsDealt_When_CompleteDealingCards_Then_DealingCompletedEvent :
    KcqrsAggregateTestSpecification<Dealer>() {
    companion object {
        val AGGREGATE_ID = DealerIdentity.create()
        val PLAYER_ID1 = PlayerIdentity.create()
        val PLAYER_ID2 = PlayerIdentity.create()
        val PLAYERS = listOf(PLAYER_ID1, PLAYER_ID2)
        val GAME_ID = GameIdentity.create()
        val CARDS = CardsHelper.allRanksWithJollyCards()
            .plus(CardsHelper.allRanksWithJollyCards())
            .shuffled()
    }

    override val aggregateId: DealerIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> Dealer = { Dealer.empty() }
    override fun streamNameRoot(): String = "DealerStream"
    override fun onCommandHandler(): IAggregateCommandHandler<Dealer> =
        AggregateDealerCommandHandler(repository, DummyExternalEventPublisherAdapter())

    override fun given(): List<IDomainEvent> {
        val events = mutableListOf<DealerEvent>(
            DeckCreated.create(AGGREGATE_ID, GAME_ID, PLAYERS, CARDS)
        )
        // Simulate all cards being dealt
        CARDS.forEach { card ->
            events.add(CardDealtToDeck.create(AGGREGATE_ID, GAME_ID, card))
        }
        return events
    }

    override fun `when`(): ICommand<Dealer> = CompleteDealingCards(aggregateId, GAME_ID)

    override fun expected(): List<IDomainEvent> = listOf(
        DealingCompleted.create(aggregateId, GAME_ID)
    )

    override fun expectedException(): Exception? = null
}

internal class Given_dealerWithCardsRemaining_When_CompleteDealingCards_Then_Exception :
    KcqrsAggregateTestSpecification<Dealer>() {
    companion object {
        val AGGREGATE_ID = DealerIdentity.create()
        val PLAYER_ID1 = PlayerIdentity.create()
        val PLAYER_ID2 = PlayerIdentity.create()
        val PLAYERS = listOf(PLAYER_ID1, PLAYER_ID2)
        val GAME_ID = GameIdentity.create()
        val CARDS = CardsHelper.allRanksWithJollyCards()
            .plus(CardsHelper.allRanksWithJollyCards())
            .shuffled()
    }

    override val aggregateId: DealerIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> Dealer = { Dealer.empty() }
    override fun streamNameRoot(): String = "DealerStream"
    override fun onCommandHandler(): IAggregateCommandHandler<Dealer> =
        AggregateDealerCommandHandler(repository, DummyExternalEventPublisherAdapter())

    override fun given(): List<IDomainEvent> = listOf(
        DeckCreated.create(AGGREGATE_ID, GAME_ID, PLAYERS, CARDS)
        // No cards dealt, so cards still available
    )

    override fun `when`(): ICommand<Dealer> = CompleteDealingCards(aggregateId, GAME_ID)

    override fun expected(): List<IDomainEvent> = listOf()

    override fun expectedException(): Exception? =
        IllegalStateException("Cards still available, dealing not completed")
}

internal class Given_nothingDealer_When_CompleteDealingCards_Then_Exception :
    KcqrsAggregateTestSpecification<Dealer>() {
    companion object {
        val AGGREGATE_ID = DealerIdentity.create()
        val GAME_ID = GameIdentity.create()
    }

    override val aggregateId: DealerIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> Dealer = { Dealer.empty() }
    override fun streamNameRoot(): String = "DealerStream"
    override fun onCommandHandler(): IAggregateCommandHandler<Dealer> =
        AggregateDealerCommandHandler(repository, DummyExternalEventPublisherAdapter())

    override fun given(): List<IDomainEvent> = listOf()

    override fun `when`(): ICommand<Dealer> = CompleteDealingCards(aggregateId, GAME_ID)

    override fun expected(): List<IDomainEvent> = listOf()

    override fun expectedException(): Exception? =
        IllegalStateException("Current dealer with id ${DealerIdentity.empty().valueAsString()} is not yet created")
}

internal class Given_dealerWithAllCardsDealt_When_CompleteDealingCardsWrongGameId_Then_Exception :
    KcqrsAggregateTestSpecification<Dealer>() {
    companion object {
        val AGGREGATE_ID = DealerIdentity.create()
        val PLAYER_ID1 = PlayerIdentity.create()
        val PLAYER_ID2 = PlayerIdentity.create()
        val PLAYERS = listOf(PLAYER_ID1, PLAYER_ID2)
        val GAME_ID = GameIdentity.create()
        val WRONG_GAME_ID = GameIdentity.create()
        val CARDS = CardsHelper.allRanksWithJollyCards()
            .plus(CardsHelper.allRanksWithJollyCards())
            .shuffled()
    }

    override val aggregateId: DealerIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> Dealer = { Dealer.empty() }
    override fun streamNameRoot(): String = "DealerStream"
    override fun onCommandHandler(): IAggregateCommandHandler<Dealer> =
        AggregateDealerCommandHandler(repository, DummyExternalEventPublisherAdapter())

    override fun given(): List<IDomainEvent> {
        val events = mutableListOf<DealerEvent>(
            DeckCreated.create(AGGREGATE_ID, GAME_ID, PLAYERS, CARDS)
        )
        // Simulate all cards being dealt
        CARDS.forEach { card ->
            events.add(CardDealtToDeck.create(AGGREGATE_ID, GAME_ID, card))
        }
        return events
    }

    override fun `when`(): ICommand<Dealer> = CompleteDealingCards(aggregateId, WRONG_GAME_ID)

    override fun expected(): List<IDomainEvent> = listOf()

    override fun expectedException(): Exception? =
        IllegalArgumentException("Game ${WRONG_GAME_ID.valueAsString()} is different")
}
