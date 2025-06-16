package com.abaddon83.burraco.game.commands.gameWaitingDealer

import com.abaddon83.burraco.game.events.game.*
import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameDraft
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.card.Rank
import com.abaddon83.burraco.common.models.card.Suit
import io.github.abaddon.kcqrs.core.IIdentity
import io.github.abaddon.kcqrs.core.domain.messages.commands.ICommand
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.github.abaddon.kcqrs.test.KcqrsAggregateTestSpecification

internal class Given_GameDraft_When_AddCardSecondPlayerDeck_Then_exception : KcqrsAggregateTestSpecification<Game>(){
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

    override fun `when`(): ICommand<Game> = AddCardSecondPlayerDeck(aggregateId, CARD)

    override fun expected(): List<IDomainEvent> = listOf()

    override fun expectedException(): Exception? = UnsupportedOperationException("Aggregate in a wrong status")
}

internal class Given_GameWaitingDealer_When_AddCardSecondPlayerDeck_Then_event : KcqrsAggregateTestSpecification<Game>(){
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

    override fun `when`(): ICommand<Game> = AddCardSecondPlayerDeck(aggregateId, CARD)

    override fun expected(): List<IDomainEvent> = listOf(
        CardAddedSecondPlayerDeck.create(aggregateId, CARD)
    )

    override fun expectedException(): Exception? = null
}

internal class Given_GameWaitingDealerWith2PlayersAndSecondPlayerDeckWith10Cards_When_AddCardSecondPlayerDeck_Then_event : KcqrsAggregateTestSpecification<Game>(){
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
        CardAddedSecondPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Ace)),
        CardAddedSecondPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Two)),
        CardAddedSecondPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Three)),
        CardAddedSecondPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Four)),
        CardAddedSecondPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Five)),
        CardAddedSecondPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Six)),
        CardAddedSecondPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Seven)),
        CardAddedSecondPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Eight)),
        CardAddedSecondPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Nine)),
        CardAddedSecondPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Ten)),
    )

    override fun `when`(): ICommand<Game> = AddCardSecondPlayerDeck(aggregateId, CARD)

    override fun expected(): List<IDomainEvent> = listOf(
        CardAddedSecondPlayerDeck.create(aggregateId, CARD)
    )

    override fun expectedException(): Exception? = null
}

internal class Given_GameWaitingDealerWith2PlayersAndSecondPlayerDeckWith11Cards_When_AddCardSecondPlayerDeck_Then_exception : KcqrsAggregateTestSpecification<Game>(){
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
        CardAddedSecondPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Ace)),
        CardAddedSecondPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Two)),
        CardAddedSecondPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Three)),
        CardAddedSecondPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Four)),
        CardAddedSecondPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Five)),
        CardAddedSecondPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Six)),
        CardAddedSecondPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Seven)),
        CardAddedSecondPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Eight)),
        CardAddedSecondPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Nine)),
        CardAddedSecondPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Ten)),
        CardAddedSecondPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Jack)),
    )

    override fun `when`(): ICommand<Game> = AddCardSecondPlayerDeck(aggregateId, CARD)

    override fun expected(): List<IDomainEvent> = listOf()

    override fun expectedException(): Exception? = IllegalStateException("The Second player deck has already enough card. (Max 11)")
}

internal class Given_GameWaitingDealerWith3PlayersAndSecondPlayerDeckWith11Cards_When_AddCardSecondPlayerDeck_Then_exception : KcqrsAggregateTestSpecification<Game>(){
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
        CardAddedSecondPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Ace)),
        CardAddedSecondPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Two)),
        CardAddedSecondPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Three)),
        CardAddedSecondPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Four)),
        CardAddedSecondPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Five)),
        CardAddedSecondPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Six)),
        CardAddedSecondPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Seven)),
        CardAddedSecondPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Eight)),
        CardAddedSecondPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Nine)),
        CardAddedSecondPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Ten)),
        CardAddedSecondPlayerDeck.create(aggregateId, Card(Suit.Heart, Rank.Jack)),
    )

    override fun `when`(): ICommand<Game> = AddCardSecondPlayerDeck(aggregateId, CARD)

    override fun expected(): List<IDomainEvent> = listOf()

    override fun expectedException(): Exception? = IllegalStateException("The Second player deck has already enough card. (Max 11)")
}