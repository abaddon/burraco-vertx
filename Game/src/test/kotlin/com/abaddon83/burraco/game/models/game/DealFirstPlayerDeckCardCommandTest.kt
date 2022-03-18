package com.abaddon83.burraco.game.models.game

import com.abaddon83.burraco.game.commands.DealFirstPlayerDeckCard
import com.abaddon83.burraco.game.commands.DealPlayerCard
import com.abaddon83.burraco.game.commands.RequestDealCards
import com.abaddon83.burraco.game.events.game.*
import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.card.Ranks
import com.abaddon83.burraco.game.models.card.Suits
import com.abaddon83.burraco.game.models.player.PlayerIdentity
import io.github.abaddon.kcqrs.core.IIdentity
import io.github.abaddon.kcqrs.core.domain.messages.commands.ICommand
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.github.abaddon.kcqrs.test.KcqrsAggregateTestSpecification

internal class `Given a GameWaitingDealer when deal card to first player deck then CardDealtWithPlayer event raised` : KcqrsAggregateTestSpecification<Game>(){
    companion object{
        val AGGREGATE_ID =GameIdentity.create()
        val PLAYER_ID1=PlayerIdentity.create()
        val PLAYER_ID2=PlayerIdentity.create()
        val CARD= Card(Suits.Clover, Ranks.Ace)
    }
    //Setup
    override val aggregateId: GameIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> GameDraft ={ GameDraft.init(it as GameIdentity) }
    override fun streamNameRoot(): String ="Stream1"

    //Test
    override fun given(): List<IDomainEvent> = listOf<GameEvent>(
        PlayerAdded.create(aggregateId,PLAYER_ID1),
        PlayerAdded.create(aggregateId,PLAYER_ID2),
        CardDealingRequested.create(aggregateId, PLAYER_ID1),
    )

    override fun `when`(): ICommand<Game> = DealFirstPlayerDeckCard(aggregateId,CARD)

    override fun expected(): List<IDomainEvent> = listOf(
        CardDealtWithFirstPlayerDeck.create(aggregateId, CARD)
    )

    override fun expectedException(): Exception? = null
}

internal class `Given a game with 2 players and first player deck with 10 cards when deal card to first player deck then CardDealtWithPlayer event raised` : KcqrsAggregateTestSpecification<Game>(){
    companion object{
        val AGGREGATE_ID =GameIdentity.create()
        val PLAYER_ID1=PlayerIdentity.create()
        val PLAYER_ID2=PlayerIdentity.create()
        val CARD= Card(Suits.Clover, Ranks.Ace)
    }
    //Setup
    override val aggregateId: GameIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> GameDraft ={ GameDraft.init(it as GameIdentity) }
    override fun streamNameRoot(): String ="Stream1"

    //Test
    override fun given(): List<IDomainEvent> = listOf<GameEvent>(
        PlayerAdded.create(aggregateId,PLAYER_ID1),
        PlayerAdded.create(aggregateId,PLAYER_ID2),
        CardDealingRequested.create(aggregateId, PLAYER_ID1),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Ace)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Two)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Three)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Four)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Five)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Six)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Seven)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Eight)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Nine)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Ten)),
    )

    override fun `when`(): ICommand<Game> = DealFirstPlayerDeckCard(aggregateId, CARD)

    override fun expected(): List<IDomainEvent> = listOf(
        CardDealtWithFirstPlayerDeck.create(aggregateId, CARD)
    )

    override fun expectedException(): Exception? = null
}

internal class `Given a game with 2 players and first player deck with 11 cards when deal card to first player deck then an exception is raised` : KcqrsAggregateTestSpecification<Game>(){
    companion object{
        val AGGREGATE_ID =GameIdentity.create()
        val PLAYER_ID1=PlayerIdentity.create()
        val CARD= Card(Suits.Clover, Ranks.Ace)
    }
    //Setup
    override val aggregateId: GameIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> GameDraft ={ GameDraft.init(it as GameIdentity) }
    override fun streamNameRoot(): String ="Stream1"

    //Test
    override fun given(): List<IDomainEvent> = listOf<GameEvent>(
        PlayerAdded.create(aggregateId,PLAYER_ID1),
        PlayerAdded.create(aggregateId,PlayerIdentity.create()),
        CardDealingRequested.create(aggregateId, PLAYER_ID1),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Ace)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Two)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Three)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Four)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Five)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Six)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Seven)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Eight)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Nine)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Ten)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Jack)),
    )

    override fun `when`(): ICommand<Game> = DealFirstPlayerDeckCard(aggregateId, CARD)

    override fun expected(): List<IDomainEvent> = listOf()

    override fun expectedException(): Exception? = IllegalStateException("The First player deck has already enough card")
}

internal class `Given a game with 3 players and first player deck with 11 cards when deal card to first player deck then CardDealtWithPlayer event raised` : KcqrsAggregateTestSpecification<Game>(){
    companion object{
        val AGGREGATE_ID =GameIdentity.create()
        val PLAYER_ID1=PlayerIdentity.create()
        val CARD= Card(Suits.Clover, Ranks.Ace)
    }
    //Setup
    override val aggregateId: GameIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> GameDraft ={ GameDraft.init(it as GameIdentity) }
    override fun streamNameRoot(): String ="Stream1"

    //Test
    override fun given(): List<IDomainEvent> = listOf<GameEvent>(
        PlayerAdded.create(aggregateId,PLAYER_ID1),
        PlayerAdded.create(aggregateId,PlayerIdentity.create()),
        PlayerAdded.create(aggregateId,PlayerIdentity.create()),
        CardDealingRequested.create(aggregateId, PLAYER_ID1),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Ace)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Two)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Three)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Four)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Five)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Six)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Seven)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Eight)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Nine)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Ten)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Jack)),
    )

    override fun `when`(): ICommand<Game> = DealFirstPlayerDeckCard(aggregateId, CARD)

    override fun expected(): List<IDomainEvent> = listOf(
        CardDealtWithFirstPlayerDeck.create(aggregateId, CARD)
    )

    override fun expectedException(): Exception? = null
}

internal class `Given a game with 3 players and first player deck with 13 cards when deal card to first player deck then an exception is raised` : KcqrsAggregateTestSpecification<Game>(){
    companion object{
        val AGGREGATE_ID =GameIdentity.create()
        val PLAYER_ID1=PlayerIdentity.create()
        val CARD= Card(Suits.Clover, Ranks.Ace)
    }
    //Setup
    override val aggregateId: GameIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> GameDraft ={ GameDraft.init(it as GameIdentity) }
    override fun streamNameRoot(): String ="Stream1"

    //Test
    override fun given(): List<IDomainEvent> = listOf<GameEvent>(
        PlayerAdded.create(aggregateId,PLAYER_ID1),
        PlayerAdded.create(aggregateId,PlayerIdentity.create()),
        PlayerAdded.create(aggregateId,PlayerIdentity.create()),
        CardDealingRequested.create(aggregateId, PLAYER_ID1),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Ace)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Two)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Three)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Four)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Five)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Six)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Seven)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Eight)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Nine)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Ten)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Jack)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.Queen)),
        CardDealtWithFirstPlayerDeck.create(aggregateId, Card(Suits.Heart, Ranks.King)),
    )

    override fun `when`(): ICommand<Game> = DealFirstPlayerDeckCard(aggregateId, CARD)

    override fun expected(): List<IDomainEvent> = listOf()

    override fun expectedException(): Exception? = IllegalStateException("The First player deck has already enough card")
}