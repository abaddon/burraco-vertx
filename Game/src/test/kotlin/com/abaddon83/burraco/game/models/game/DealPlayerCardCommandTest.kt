package com.abaddon83.burraco.game.models.game

import com.abaddon83.burraco.game.commands.DealPlayerCard
import com.abaddon83.burraco.game.commands.RequestDealCards
import com.abaddon83.burraco.game.events.game.CardDealingRequested
import com.abaddon83.burraco.game.events.game.CardDealtWithPlayer
import com.abaddon83.burraco.game.events.game.GameEvent
import com.abaddon83.burraco.game.events.game.PlayerAdded
import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.card.Ranks
import com.abaddon83.burraco.game.models.card.Suits
import com.abaddon83.burraco.game.models.player.PlayerIdentity
import io.github.abaddon.kcqrs.core.IIdentity
import io.github.abaddon.kcqrs.core.domain.messages.commands.ICommand
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.github.abaddon.kcqrs.test.KcqrsAggregateTestSpecification

internal class `Given a GameWaitingDealer when deal card player then CardDealtWithPlayer event raised` : KcqrsAggregateTestSpecification<Game>(){
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

    override fun `when`(): ICommand<Game> = DealPlayerCard(aggregateId,PLAYER_ID1,CARD)

    override fun expected(): List<IDomainEvent> = listOf(
        CardDealtWithPlayer.create(aggregateId, PLAYER_ID1, CARD)
    )

    override fun expectedException(): Exception? = null
}

internal class `Given a player with 10 cards when deal card player then CardDealtWithPlayer event raised` : KcqrsAggregateTestSpecification<Game>(){
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
        CardDealtWithPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Ace)),
        CardDealtWithPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Two)),
        CardDealtWithPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Three)),
        CardDealtWithPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Four)),
        CardDealtWithPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Five)),
        CardDealtWithPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Six)),
        CardDealtWithPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Seven)),
        CardDealtWithPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Eight)),
        CardDealtWithPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Nine)),
        CardDealtWithPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Ten)),
    )

    override fun `when`(): ICommand<Game> = DealPlayerCard(aggregateId,PLAYER_ID1,CARD)

    override fun expected(): List<IDomainEvent> = listOf(
        CardDealtWithPlayer.create(aggregateId, PLAYER_ID1, CARD)
    )

    override fun expectedException(): Exception? = null
}

internal class `Given a player with 11 cards when deal card player then an exception is raised` : KcqrsAggregateTestSpecification<Game>(){
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
        CardDealtWithPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Ace)),
        CardDealtWithPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Two)),
        CardDealtWithPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Three)),
        CardDealtWithPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Four)),
        CardDealtWithPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Five)),
        CardDealtWithPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Six)),
        CardDealtWithPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Seven)),
        CardDealtWithPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Eight)),
        CardDealtWithPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Nine)),
        CardDealtWithPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Ten)),
        CardDealtWithPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Jack)),
    )

    override fun `when`(): ICommand<Game> = DealPlayerCard(aggregateId,PLAYER_ID1,CARD)

    override fun expected(): List<IDomainEvent> = listOf()

    override fun expectedException(): Exception? = IllegalStateException("Player ${PLAYER_ID1.valueAsString()} has too many cards (12)")
}