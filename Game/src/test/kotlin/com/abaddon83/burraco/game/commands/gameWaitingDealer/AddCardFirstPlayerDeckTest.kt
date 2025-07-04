package com.abaddon83.burraco.game.commands.gameWaitingDealer

import com.abaddon83.burraco.common.models.card.Card
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameDraft
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.card.Rank
import com.abaddon83.burraco.common.models.card.Suit
import com.abaddon83.burraco.common.models.event.game.CardAddedFirstPlayerDeck
import com.abaddon83.burraco.common.models.event.game.CardDealingRequested
import com.abaddon83.burraco.common.models.event.game.GameCreated
import com.abaddon83.burraco.common.models.event.game.GameEvent
import com.abaddon83.burraco.common.models.event.game.PlayerAdded
import io.github.abaddon.kcqrs.core.IIdentity
import io.github.abaddon.kcqrs.core.domain.messages.commands.ICommand
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.github.abaddon.kcqrs.test.KcqrsAggregateTestSpecification

internal class Given_GameDraft_When_addCardFirstPlayerDeck_Then_exception : KcqrsAggregateTestSpecification<Game>(){
    companion object{
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID1=PlayerIdentity.create()
        val PLAYER_ID2=PlayerIdentity.create()
        val CARD= Card(Suit.Clover, Rank.Ace)
    }
    //Setup
    override val aggregateId: GameIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> GameDraft = { id -> GameDraft.empty() }
    override fun streamNameRoot(): String ="Stream1"
    override fun membersToIgnore(): List<String> = listOf("header")

    //Test
    override fun given(): List<IDomainEvent> = listOf<GameEvent>(
        GameCreated.create(aggregateId),
        PlayerAdded.create(aggregateId, PLAYER_ID1),
        PlayerAdded.create(aggregateId, PLAYER_ID2)
    )

    override fun `when`(): ICommand<Game> = AddCardFirstPlayerDeck(aggregateId, CARD)

    override fun expected(): List<IDomainEvent> = listOf()

    override fun expectedException(): Exception? = UnsupportedOperationException("Aggregate in a wrong status")
}

internal class Given_GameWaitingDealer_When_addCardFirstPlayerDeck_Then_event : KcqrsAggregateTestSpecification<Game>(){
    companion object{
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID1=PlayerIdentity.create()
        val PLAYER_ID2=PlayerIdentity.create()
        val CARD= Card(Suit.Clover, Rank.Ace)
    }
    //Setup
    override val aggregateId: GameIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> GameDraft = { id -> GameDraft.empty() }
    override fun streamNameRoot(): String ="Stream1"
    override fun membersToIgnore(): List<String> = listOf("header")

    //Test
    override fun given(): List<IDomainEvent> = listOf<GameEvent>(
        GameCreated.create(aggregateId),
        PlayerAdded.create(aggregateId, PLAYER_ID1),
        PlayerAdded.create(aggregateId, PLAYER_ID2),
        CardDealingRequested.create(aggregateId, PLAYER_ID1),
    )

    override fun `when`(): ICommand<Game> = AddCardFirstPlayerDeck(aggregateId, CARD)

    override fun expected(): List<IDomainEvent> = listOf(
        CardAddedFirstPlayerDeck.create(aggregateId, CARD)
    )

    override fun expectedException(): Exception? = null
}

internal class Given_GameWaitingDealerWith2PlayersAndFirstDeck10Cards_When_addCardFirstPlayerDeck_Then_event : KcqrsAggregateTestSpecification<Game>(){
    companion object{
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID1=PlayerIdentity.create()
        val PLAYER_ID2=PlayerIdentity.create()
        val CARD= Card(Suit.Clover, Rank.Ace)
    }
    //Setup
    override val aggregateId: GameIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> GameDraft = { id -> GameDraft.empty() }
    override fun streamNameRoot(): String ="Stream1"
    override fun membersToIgnore(): List<String> = listOf("header")

    //Test
    override fun given(): List<IDomainEvent> = listOf<GameEvent>(
        GameCreated.create(aggregateId),
        PlayerAdded.create(aggregateId, PLAYER_ID1),
        PlayerAdded.create(aggregateId, PLAYER_ID2),
        CardDealingRequested.create(aggregateId, PLAYER_ID1),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Ace)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Two)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Three)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Four)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Five)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Six)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Seven)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Eight)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Nine)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Ten)),
    )

    override fun `when`(): ICommand<Game> = AddCardFirstPlayerDeck(aggregateId, CARD)

    override fun expected(): List<IDomainEvent> = listOf(
        CardAddedFirstPlayerDeck.create(aggregateId, CARD)
    )

    override fun expectedException(): Exception? = null
}

internal class Given_GameWaitingDealerWith2PlayersAndFirstDeck11Cards_When_addCardFirstPlayerDeck_Then_exception : KcqrsAggregateTestSpecification<Game>(){
    companion object{
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID1=PlayerIdentity.create()
        val CARD= Card(Suit.Clover, Rank.Ace)
    }
    //Setup
    override val aggregateId: GameIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> GameDraft = { id -> GameDraft.empty() }
    override fun streamNameRoot(): String ="Stream1"
    override fun membersToIgnore(): List<String> = listOf("header")

    //Test
    override fun given(): List<IDomainEvent> = listOf<GameEvent>(
        GameCreated.create(aggregateId),
        PlayerAdded.create(aggregateId, PLAYER_ID1),
        PlayerAdded.create(aggregateId,PlayerIdentity.create()),
        CardDealingRequested.create(aggregateId, PLAYER_ID1),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Ace)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Two)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Three)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Four)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Five)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Six)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Seven)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Eight)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Nine)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Ten)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Jack)),
    )

    override fun `when`(): ICommand<Game> = AddCardFirstPlayerDeck(aggregateId, CARD)

    override fun expected(): List<IDomainEvent> = listOf()

    override fun expectedException(): Exception? = IllegalStateException("The First player deck has already enough card. (Max 11)")
}

internal class Given_GameWaitingDealerWith3PlayersAndFirstDeck11Cards_When_addCardFirstPlayerDeck_Then_event : KcqrsAggregateTestSpecification<Game>(){
    companion object{
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID1=PlayerIdentity.create()
        val CARD= Card(Suit.Clover, Rank.Ace)
    }
    //Setup
    override val aggregateId: GameIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> GameDraft = { id -> GameDraft.empty() }
    override fun streamNameRoot(): String ="Stream1"
    override fun membersToIgnore(): List<String> = listOf("header")

    //Test
    override fun given(): List<IDomainEvent> = listOf<GameEvent>(
        GameCreated.create(aggregateId),
        PlayerAdded.create(aggregateId, PLAYER_ID1),
        PlayerAdded.create(aggregateId,PlayerIdentity.create()),
        PlayerAdded.create(aggregateId,PlayerIdentity.create()),
        CardDealingRequested.create(aggregateId, PLAYER_ID1),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Ace)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Two)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Three)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Four)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Five)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Six)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Seven)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Eight)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Nine)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Ten)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Jack)),
    )

    override fun `when`(): ICommand<Game> = AddCardFirstPlayerDeck(aggregateId, CARD)

    override fun expected(): List<IDomainEvent> = listOf(
        CardAddedFirstPlayerDeck.create(aggregateId, CARD)
    )

    override fun expectedException(): Exception? = null
}

internal class Given_GameWaitingDealerWith3PlayersAndFirstDeck13Cards_When_addCardFirstPlayerDeck_Then_exception : KcqrsAggregateTestSpecification<Game>(){
    companion object{
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID1=PlayerIdentity.create()
        val CARD= Card(Suit.Clover, Rank.Ace)
    }
    //Setup
    override val aggregateId: GameIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> GameDraft = { id -> GameDraft.empty() }
    override fun streamNameRoot(): String ="Stream1"
    override fun membersToIgnore(): List<String> = listOf("header")

    //Test
    override fun given(): List<IDomainEvent> = listOf<GameEvent>(
        GameCreated.create(aggregateId),
        PlayerAdded.create(aggregateId, PLAYER_ID1),
        PlayerAdded.create(aggregateId,PlayerIdentity.create()),
        PlayerAdded.create(aggregateId,PlayerIdentity.create()),
        CardDealingRequested.create(aggregateId, PLAYER_ID1),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Ace)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Two)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Three)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Four)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Five)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Six)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Seven)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Eight)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Nine)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Ten)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Jack)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Queen)),
        CardAddedFirstPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.King)),
    )

    override fun `when`(): ICommand<Game> = AddCardFirstPlayerDeck(aggregateId, CARD)

    override fun expected(): List<IDomainEvent> = listOf()

    override fun expectedException(): Exception? = IllegalStateException("The First player deck has already enough card. (Max 13)")
}