package com.abaddon83.burraco.game.commands.gameWaitingDealer

import com.abaddon83.burraco.game.events.game.*
import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.card.Ranks
import com.abaddon83.burraco.game.models.card.Suits
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameDraft
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import io.github.abaddon.kcqrs.core.IIdentity
import io.github.abaddon.kcqrs.core.domain.messages.commands.ICommand
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.github.abaddon.kcqrs.test.KcqrsAggregateTestSpecification

internal class Given_GameWaitingDealer_When_AddCard_Then_eventPlayer: KcqrsAggregateTestSpecification<Game>(){
    companion object{
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID1=PlayerIdentity.create()
        val PLAYER_ID2=PlayerIdentity.create()
        val CARD= Card(Suits.Clover, Ranks.Ace)
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
        val CARD= Card(Suits.Clover, Ranks.Ace)
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
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Ace)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Two)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Three)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Four)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Five)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Six)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Seven)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Eight)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Nine)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Ten)),
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
        val CARD= Card(Suits.Clover, Ranks.Ace)
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
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Ace)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Two)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Three)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Four)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Five)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Six)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Seven)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Eight)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Nine)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Ten)),
        CardAddedPlayer.create(aggregateId, PLAYER_ID1, Card(Suits.Heart, Ranks.Jack)),
    )

    override fun `when`(): ICommand<Game> = AddCardPlayer(aggregateId, PLAYER_ID1, CARD)

    override fun expected(): List<IDomainEvent> = listOf()

    override fun expectedException(): Exception? = IllegalStateException("Player ${PLAYER_ID1.valueAsString()} has already enough card. (Max 11)")
}