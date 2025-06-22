package com.abaddon83.burraco.dealer.commands

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.dealer.events.CardDealtToPlayerDeck2
import com.abaddon83.burraco.dealer.events.DealerEvent
import com.abaddon83.burraco.dealer.events.DeckCreated
import com.abaddon83.burraco.dealer.helpers.CardsHelper
import com.abaddon83.burraco.dealer.models.Dealer
import com.abaddon83.burraco.common.models.DealerIdentity
import com.abaddon83.burraco.helper.DummyExternalEventPublisherAdapter
import io.github.abaddon.kcqrs.core.IIdentity
import io.github.abaddon.kcqrs.core.domain.IAggregateCommandHandler
import io.github.abaddon.kcqrs.core.domain.messages.commands.ICommand
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.github.abaddon.kcqrs.test.KcqrsAggregateTestSpecification

internal class Given_existingDealer_When_DealCardToPlayerDeck2_Then_event : KcqrsAggregateTestSpecification<Dealer>() {
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

    //Test
    override fun given(): List<IDomainEvent> = listOf<DealerEvent>(
        DeckCreated.create(AGGREGATE_ID, GAME_ID, PLAYERS, CARDS)
    )

    override fun `when`(): ICommand<Dealer> = DealCardToPlayerDeck2(aggregateId, gameIdentity = GAME_ID)

    override fun expected(): List<IDomainEvent> = listOf(
        CardDealtToPlayerDeck2.create(aggregateId, GAME_ID, CARDS.first())
    )

    override fun expectedException(): Exception? = null
}

internal class Given_nothing_When_DealCardToPlayerDeck2_Then_exception : KcqrsAggregateTestSpecification<Dealer>() {
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
        AggregateDealerCommandHandler(repository, DummyExternalEventPublisherAdapter())

    //Test
    override fun given(): List<IDomainEvent> = listOf<DealerEvent>(

    )

    override fun `when`(): ICommand<Dealer> = DealCardToPlayerDeck2(aggregateId, gameIdentity = GAME_ID)

    override fun expected(): List<IDomainEvent> = listOf(
    )

    override fun expectedException(): Exception? =
        IllegalStateException("Current dealer with id ${DealerIdentity.empty().valueAsString()} is not yet created")
}

internal class Given_existingDealer_When_DealCardToPlayerDeck2WithWrongGamId_Then_exception :
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
        AggregateDealerCommandHandler(repository, DummyExternalEventPublisherAdapter())

    //Test
    override fun given(): List<IDomainEvent> = listOf<DealerEvent>(
        DeckCreated.create(AGGREGATE_ID, GAME_ID, PLAYERS, CARDS)
    )

    override fun `when`(): ICommand<Dealer> = DealCardToPlayerDeck2(aggregateId, gameIdentity = WRONG_GAME_ID)

    override fun expected(): List<IDomainEvent> = listOf(

    )

    override fun expectedException(): Exception? =
        IllegalArgumentException("Game ${WRONG_GAME_ID.valueAsString()} is different")
}

internal class Given_existingDealerWith4PlayerWithDealCardToPlayerDeck2WithMaxCards_When_DealCardToPlayerDeck2_Then_exception :
    KcqrsAggregateTestSpecification<Dealer>() {
    companion object {
        val AGGREGATE_ID = DealerIdentity.create()
        val PLAYER_ID1 = PlayerIdentity.create()
        val PLAYER_ID2 = PlayerIdentity.create()
        val PLAYER_ID3 = PlayerIdentity.create()
        val PLAYER_ID4 = PlayerIdentity.create()
        val PLAYERS = listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3, PLAYER_ID4)
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
        AggregateDealerCommandHandler(repository, DummyExternalEventPublisherAdapter())

    //Test
    override fun given(): List<IDomainEvent> = listOf<DealerEvent>(
        DeckCreated.create(AGGREGATE_ID, GAME_ID, PLAYERS, CARDS),
        CardDealtToPlayerDeck2.create(aggregateId, GAME_ID, CARDS.elementAt(0)),
        CardDealtToPlayerDeck2.create(aggregateId, GAME_ID, CARDS.elementAt(1)),
        CardDealtToPlayerDeck2.create(aggregateId, GAME_ID, CARDS.elementAt(2)),
        CardDealtToPlayerDeck2.create(aggregateId, GAME_ID, CARDS.elementAt(3)),
        CardDealtToPlayerDeck2.create(aggregateId, GAME_ID, CARDS.elementAt(4)),
        CardDealtToPlayerDeck2.create(aggregateId, GAME_ID, CARDS.elementAt(5)),
        CardDealtToPlayerDeck2.create(aggregateId, GAME_ID, CARDS.elementAt(6)),
        CardDealtToPlayerDeck2.create(aggregateId, GAME_ID, CARDS.elementAt(7)),
        CardDealtToPlayerDeck2.create(aggregateId, GAME_ID, CARDS.elementAt(8)),
        CardDealtToPlayerDeck2.create(aggregateId, GAME_ID, CARDS.elementAt(9)),
        CardDealtToPlayerDeck2.create(aggregateId, GAME_ID, CARDS.elementAt(10))
    )

    override fun `when`(): ICommand<Dealer> = DealCardToPlayerDeck2(aggregateId, gameIdentity = GAME_ID)

    override fun expected(): List<IDomainEvent> = listOf(

    )

    override fun expectedException(): Exception? =
        IllegalStateException("The playerDeck2NumCards has the maximum number of cards (11)")
}

internal class Given_existingDealerWith3PlayerWithDealCardToPlayerDeck2With11Cards_When_DealCardToPlayerDeck2_Then_event :
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
        AggregateDealerCommandHandler(repository, DummyExternalEventPublisherAdapter())

    //Test
    override fun given(): List<IDomainEvent> = listOf<DealerEvent>(
        DeckCreated.create(AGGREGATE_ID, GAME_ID, PLAYERS, CARDS),
        CardDealtToPlayerDeck2.create(aggregateId, GAME_ID, CARDS.elementAt(0)),
        CardDealtToPlayerDeck2.create(aggregateId, GAME_ID, CARDS.elementAt(1)),
        CardDealtToPlayerDeck2.create(aggregateId, GAME_ID, CARDS.elementAt(2)),
        CardDealtToPlayerDeck2.create(aggregateId, GAME_ID, CARDS.elementAt(3)),
        CardDealtToPlayerDeck2.create(aggregateId, GAME_ID, CARDS.elementAt(4)),
        CardDealtToPlayerDeck2.create(aggregateId, GAME_ID, CARDS.elementAt(5)),
        CardDealtToPlayerDeck2.create(aggregateId, GAME_ID, CARDS.elementAt(6)),
        CardDealtToPlayerDeck2.create(aggregateId, GAME_ID, CARDS.elementAt(7)),
        CardDealtToPlayerDeck2.create(aggregateId, GAME_ID, CARDS.elementAt(8)),
        CardDealtToPlayerDeck2.create(aggregateId, GAME_ID, CARDS.elementAt(9)),
        CardDealtToPlayerDeck2.create(aggregateId, GAME_ID, CARDS.elementAt(10))
    )

    override fun `when`(): ICommand<Dealer> = DealCardToPlayerDeck2(aggregateId, gameIdentity = GAME_ID)

    override fun expected(): List<IDomainEvent> = listOf()

    override fun expectedException(): Exception? =
        IllegalStateException("The playerDeck2NumCards has the maximum number of cards (11)")
}