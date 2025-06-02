package com.abaddon83.burraco.dealer.commands

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.dealer.events.CardDealtToPlayer
import com.abaddon83.burraco.dealer.events.DealerEvent
import com.abaddon83.burraco.dealer.events.DeckCreated
import com.abaddon83.burraco.dealer.helpers.CardsHelper
import com.abaddon83.burraco.dealer.models.Dealer
import com.abaddon83.burraco.common.models.DealerIdentity
import com.abaddon83.burraco.testHelpers.DummyExternalEventPublisherAdapter
import io.github.abaddon.kcqrs.core.IIdentity
import io.github.abaddon.kcqrs.core.domain.IAggregateCommandHandler
import io.github.abaddon.kcqrs.core.domain.messages.commands.ICommand
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.github.abaddon.kcqrs.test.KcqrsAggregateTestSpecification

internal class Given_existingDealer_When_DealCardToPlayer_Then_event : KcqrsAggregateTestSpecification<Dealer>() {
    companion object {
        val AGGREGATE_ID = DealerIdentity.create()
        val PLAYER_ID1 = PlayerIdentity.create()
        val PLAYER_ID2 = PlayerIdentity.create()
        val PLAYER_ID3 = PlayerIdentity.create()
        val PLAYERS = listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3)
        val GAME_ID = GameIdentity.create()
        val CARDS = CardsHelper.allRanksWithJollyCards()
            .plus(CardsHelper.allRanksWithJollyCards())
            .shuffled()

    }

    //Setup
    override val aggregateId: DealerIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> Dealer = { Dealer.empty() }
    override fun streamNameRoot(): String = "Stream1"
    override fun onCommandHandler(): IAggregateCommandHandler<Dealer> =
        AggregateDealerCommandHandler(repository, DummyExternalEventPublisherAdapter(), testScope.coroutineContext)

    //Test
    override fun given(): List<IDomainEvent> = listOf<DealerEvent>(
        DeckCreated.create(AGGREGATE_ID, GAME_ID, PLAYERS, CARDS)
    )

    override fun `when`(): ICommand<Dealer> = DealCardToPlayer(aggregateId, gameIdentity = GAME_ID, player = PLAYER_ID1)

    override fun expected(): List<IDomainEvent> = listOf(
        CardDealtToPlayer.create(aggregateId, GAME_ID, PLAYER_ID1, CARDS.first())
    )

    override fun expectedException(): Exception? = null
}

internal class Given_nothing_When_DealCardToPlayer_Then_exception : KcqrsAggregateTestSpecification<Dealer>() {
    companion object {
        val AGGREGATE_ID = DealerIdentity.create()
        val PLAYER_ID1 = PlayerIdentity.create()
        val PLAYER_ID2 = PlayerIdentity.create()
        val PLAYER_ID3 = PlayerIdentity.create()
        val PLAYERS = listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3)
        val GAME_ID = GameIdentity.create()
        val CARDS = CardsHelper.allRanksWithJollyCards()
            .plus(CardsHelper.allRanksWithJollyCards())
            .shuffled()

    }

    //Setup
    override val aggregateId: DealerIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> Dealer = { Dealer.empty() }
    override fun streamNameRoot(): String = "Stream1"
    override fun onCommandHandler(): IAggregateCommandHandler<Dealer> =
        AggregateDealerCommandHandler(repository, DummyExternalEventPublisherAdapter(),testScope.coroutineContext)

    //Test
    override fun given(): List<IDomainEvent> = listOf<DealerEvent>(

    )

    override fun `when`(): ICommand<Dealer> = DealCardToPlayer(aggregateId, gameIdentity = GAME_ID, player = PLAYER_ID1)

    override fun expected(): List<IDomainEvent> = listOf(
    )

    override fun expectedException(): Exception? =
        IllegalStateException("Current dealer with id ${DealerIdentity.empty().valueAsString()} is not yet created")
}

internal class Given_existingDealer_When_DealCardToPlayerWithWrongGamId_Then_exception :
    KcqrsAggregateTestSpecification<Dealer>() {
    companion object {
        val AGGREGATE_ID = DealerIdentity.create()
        val PLAYER_ID1 = PlayerIdentity.create()
        val PLAYER_ID2 = PlayerIdentity.create()
        val PLAYER_ID3 = PlayerIdentity.create()
        val PLAYERS = listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3)
        val GAME_ID = GameIdentity.create()
        val CARDS = CardsHelper.allRanksWithJollyCards()
            .plus(CardsHelper.allRanksWithJollyCards())
            .shuffled()
        val WRONG_GAME_ID = GameIdentity.create()

    }

    //Setup
    override val aggregateId: DealerIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> Dealer = { Dealer.empty() }
    override fun streamNameRoot(): String = "Stream1"
    override fun onCommandHandler(): IAggregateCommandHandler<Dealer> =
        AggregateDealerCommandHandler(repository, DummyExternalEventPublisherAdapter(),testScope.coroutineContext)

    //Test
    override fun given(): List<IDomainEvent> = listOf<DealerEvent>(
        DeckCreated.create(AGGREGATE_ID, GAME_ID, PLAYERS, CARDS)
    )

    override fun `when`(): ICommand<Dealer> =
        DealCardToPlayer(aggregateId, gameIdentity = WRONG_GAME_ID, player = PLAYER_ID1)

    override fun expected(): List<IDomainEvent> = listOf()

    override fun expectedException(): Exception? =
        IllegalArgumentException("Game ${WRONG_GAME_ID.valueAsString()} is different")
}

internal class Given_existingDealer_When_DealCardToPlayerWithWrongPlayerId_Then_exception :
    KcqrsAggregateTestSpecification<Dealer>() {
    companion object {
        val AGGREGATE_ID = DealerIdentity.create()
        val PLAYER_ID1 = PlayerIdentity.create()
        val PLAYER_ID2 = PlayerIdentity.create()
        val PLAYER_ID3 = PlayerIdentity.create()
        val PLAYERS = listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3)
        val GAME_ID = GameIdentity.create()
        val CARDS = CardsHelper.allRanksWithJollyCards()
            .plus(CardsHelper.allRanksWithJollyCards())
            .shuffled()
        val PLAYER_ID_WRONG = PlayerIdentity.create()

    }

    //Setup
    override val aggregateId: DealerIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> Dealer = { Dealer.empty() }
    override fun streamNameRoot(): String = "Stream1"
    override fun onCommandHandler(): IAggregateCommandHandler<Dealer> =
        AggregateDealerCommandHandler(repository, DummyExternalEventPublisherAdapter(),testScope.coroutineContext)

    //Test
    override fun given(): List<IDomainEvent> = listOf<DealerEvent>(
        DeckCreated.create(AGGREGATE_ID, GAME_ID, PLAYERS, CARDS)
    )

    override fun `when`(): ICommand<Dealer> =
        DealCardToPlayer(aggregateId, gameIdentity = GAME_ID, player = PLAYER_ID_WRONG)

    override fun expected(): List<IDomainEvent> = listOf(

    )

    override fun expectedException(): Exception? =
        IllegalArgumentException("Player ${PLAYER_ID_WRONG.valueAsString()} is not part of the game")
}

internal class Given_existingDealerWithPlayerWithMaxCards_When_DealCardToPlayer_Then_exception :
    KcqrsAggregateTestSpecification<Dealer>() {
    companion object {
        val AGGREGATE_ID = DealerIdentity.create()
        val PLAYER_ID1 = PlayerIdentity.create()
        val PLAYER_ID2 = PlayerIdentity.create()
        val PLAYER_ID3 = PlayerIdentity.create()
        val PLAYERS = listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3)
        val GAME_ID = GameIdentity.create()
        val CARDS = CardsHelper.allRanksWithJollyCards()
            .plus(CardsHelper.allRanksWithJollyCards())
            .shuffled()

    }

    //Setup
    override val aggregateId: DealerIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> Dealer = { Dealer.empty() }
    override fun streamNameRoot(): String = "Stream1"
    override fun onCommandHandler(): IAggregateCommandHandler<Dealer> =
        AggregateDealerCommandHandler(repository, DummyExternalEventPublisherAdapter(),testScope.coroutineContext)

    //Test
    override fun given(): List<IDomainEvent> = listOf<DealerEvent>(
        DeckCreated.create(AGGREGATE_ID, GAME_ID, PLAYERS, CARDS),
        CardDealtToPlayer.create(aggregateId, GAME_ID, PLAYER_ID1, CARDS.elementAt(0)),
        CardDealtToPlayer.create(aggregateId, GAME_ID, PLAYER_ID1, CARDS.elementAt(1)),
        CardDealtToPlayer.create(aggregateId, GAME_ID, PLAYER_ID1, CARDS.elementAt(2)),
        CardDealtToPlayer.create(aggregateId, GAME_ID, PLAYER_ID1, CARDS.elementAt(3)),
        CardDealtToPlayer.create(aggregateId, GAME_ID, PLAYER_ID1, CARDS.elementAt(4)),
        CardDealtToPlayer.create(aggregateId, GAME_ID, PLAYER_ID1, CARDS.elementAt(5)),
        CardDealtToPlayer.create(aggregateId, GAME_ID, PLAYER_ID1, CARDS.elementAt(6)),
        CardDealtToPlayer.create(aggregateId, GAME_ID, PLAYER_ID1, CARDS.elementAt(7)),
        CardDealtToPlayer.create(aggregateId, GAME_ID, PLAYER_ID1, CARDS.elementAt(8)),
        CardDealtToPlayer.create(aggregateId, GAME_ID, PLAYER_ID1, CARDS.elementAt(9)),
        CardDealtToPlayer.create(aggregateId, GAME_ID, PLAYER_ID1, CARDS.elementAt(10))
    )

    override fun `when`(): ICommand<Dealer> = DealCardToPlayer(aggregateId, gameIdentity = GAME_ID, player = PLAYER_ID1)

    override fun expected(): List<IDomainEvent> = listOf(

    )

    override fun expectedException(): Exception? =
        IllegalStateException("The player ${PLAYER_ID1.valueAsString()} has the maximum number of cards (11)")
}