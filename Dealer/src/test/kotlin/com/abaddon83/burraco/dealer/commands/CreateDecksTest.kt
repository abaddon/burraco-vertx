package com.abaddon83.burraco.dealer.commands

import com.abaddon83.burraco.common.models.DealerIdentity
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.event.dealer.DealerEvent
import com.abaddon83.burraco.common.models.event.dealer.DeckCreated
import com.abaddon83.burraco.dealer.models.Dealer
import com.abaddon83.burraco.helper.DummyExternalEventPublisherAdapter
import io.github.abaddon.kcqrs.core.IIdentity
import io.github.abaddon.kcqrs.core.domain.IAggregateCommandHandler
import io.github.abaddon.kcqrs.core.domain.messages.commands.ICommand
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.github.abaddon.kcqrs.test.KcqrsAggregateTestSpecification

internal class Given_Nothing_When_CreatDeck_Then_event : KcqrsAggregateTestSpecification<Dealer>() {
    companion object {
        val AGGREGATE_ID = DealerIdentity.create()
        val PLAYER_ID1 = PlayerIdentity.create()
        val PLAYER_ID2 = PlayerIdentity.create()
        val PLAYER_ID3 = PlayerIdentity.create()
        val PLAYERS = listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3)
        val GAME_ID = GameIdentity.create()

    }

    //Setup
    override val aggregateId: DealerIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> Dealer = { Dealer.empty() }
    override fun streamNameRoot(): String = "Stream1"
    override fun onCommandHandler(): IAggregateCommandHandler<Dealer> =
        AggregateDealerCommandHandler(repository, DummyExternalEventPublisherAdapter())

    //Test
    override fun given(): List<IDomainEvent> = listOf<DealerEvent>()

    override fun `when`(): ICommand<Dealer> = CreateDecks(aggregateId, gameIdentity = GAME_ID, players = PLAYERS)

    override fun expected(): List<IDomainEvent> = listOf(
        DeckCreated.create(AGGREGATE_ID, GAME_ID, PLAYERS, listOf())
    )

    override fun membersToIgnore(): List<String> = listOf("cards")

    override fun expectedException(): Exception? = null
}

internal class Given_existingDealer_When_CreatDeck_Then_exception : KcqrsAggregateTestSpecification<Dealer>() {
    companion object {
        val AGGREGATE_ID = DealerIdentity.create()
        val PLAYER_ID1 = PlayerIdentity.create()
        val PLAYER_ID2 = PlayerIdentity.create()
        val PLAYER_ID3 = PlayerIdentity.create()
        val PLAYERS = listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3)
        val GAME_ID = GameIdentity.create()

    }

    //Setup
    override val aggregateId: DealerIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> Dealer = { Dealer.empty() }
    override fun streamNameRoot(): String = "Stream1"
    override fun onCommandHandler(): IAggregateCommandHandler<Dealer> =
        AggregateDealerCommandHandler(repository, DummyExternalEventPublisherAdapter())

    //Test
    override fun given(): List<IDomainEvent> = listOf<DealerEvent>(
        DeckCreated.create(AGGREGATE_ID, GAME_ID, PLAYERS, listOf())
    )

    override fun `when`(): ICommand<Dealer> = CreateDecks(aggregateId, gameIdentity = GAME_ID, players = PLAYERS)

    override fun expected(): List<IDomainEvent> = listOf()

    override fun membersToIgnore(): List<String> = listOf("cards")

    override fun expectedException(): Exception? =
        IllegalStateException("Current dealer with id ${AGGREGATE_ID.valueAsString()} is already created")
}