package com.abaddon83.burraco.game.commands.gameWaitingDealer

import com.abaddon83.burraco.common.models.card.Card
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameDraft
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.card.Rank
import com.abaddon83.burraco.common.models.card.Suit
import com.abaddon83.burraco.common.models.event.game.CardAddedPlayer
import com.abaddon83.burraco.common.models.event.game.CardDealingRequested
import com.abaddon83.burraco.common.models.event.game.GameCreated
import com.abaddon83.burraco.common.models.event.game.GameEvent
import com.abaddon83.burraco.common.models.event.game.PlayerAdded
import io.github.abaddon.kcqrs.core.IIdentity
import io.github.abaddon.kcqrs.core.domain.messages.commands.ICommand
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.github.abaddon.kcqrs.test.KcqrsAggregateTestSpecification

internal class Given_GameWaitingDealer_When_AddCard_Then_eventPlayer: KcqrsAggregateTestSpecification<Game>(){
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

    override fun `when`(): ICommand<Game> = AddCardPlayer(aggregateId, PLAYER_ID1, CARD)

    override fun expected(): List<IDomainEvent> = listOf(
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, CARD)
    )

    override fun expectedException(): Exception? = null
}

internal class Given_GameWaitingDealerWithPlayerWith10Cards_When_AddCard_Then_eventPlayer : KcqrsAggregateTestSpecification<Game>(){
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
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suit.Heart, Rank.Ace)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suit.Heart, Rank.Two)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suit.Heart, Rank.Three)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suit.Heart, Rank.Four)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suit.Heart, Rank.Five)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suit.Heart, Rank.Six)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suit.Heart, Rank.Seven)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suit.Heart, Rank.Eight)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suit.Heart, Rank.Nine)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suit.Heart, Rank.Ten)),
    )

    override fun `when`(): ICommand<Game> = AddCardPlayer(aggregateId, PLAYER_ID1, CARD)

    override fun expected(): List<IDomainEvent> = listOf(
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, CARD)
    )

    override fun expectedException(): Exception? = null
}

internal class Given_GameWaitingDealerWithPlayerWith11Cards_When_AddCard_Then_exceptionPlayer : KcqrsAggregateTestSpecification<Game>(){
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
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suit.Heart, Rank.Ace)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suit.Heart, Rank.Two)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suit.Heart, Rank.Three)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suit.Heart, Rank.Four)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suit.Heart, Rank.Five)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suit.Heart, Rank.Six)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suit.Heart, Rank.Seven)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suit.Heart, Rank.Eight)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suit.Heart, Rank.Nine)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suit.Heart, Rank.Ten)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suit.Heart, Rank.Jack)),
    )

    override fun `when`(): ICommand<Game> = AddCardPlayer(aggregateId, PLAYER_ID1, CARD)

    override fun expected(): List<IDomainEvent> = listOf()

    override fun expectedException(): Exception? = IllegalStateException("Player ${PLAYER_ID1.valueAsString()} has already enough card. (Max 11)")
}