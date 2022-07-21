package com.abaddon83.burraco.game.commands.gameDraft

import com.abaddon83.burraco.game.events.game.CardDealingRequested
import com.abaddon83.burraco.game.events.game.GameCreated
import com.abaddon83.burraco.game.events.game.GameEvent
import com.abaddon83.burraco.game.events.game.PlayerAdded
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameDraft
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import io.github.abaddon.kcqrs.core.IIdentity
import io.github.abaddon.kcqrs.core.domain.messages.commands.ICommand
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.github.abaddon.kcqrs.test.KcqrsAggregateTestSpecification

internal class Given_GameDraftWith4Player_When_RequestDealCards_Then_event : KcqrsAggregateTestSpecification<Game>(){
    companion object{
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID1=PlayerIdentity.create()
        val PLAYER_ID2=PlayerIdentity.create()
        val PLAYER_ID3=PlayerIdentity.create()
        val PLAYER_ID4=PlayerIdentity.create()
    }
    //Setup
    override val aggregateId: GameIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> GameDraft ={ GameDraft.empty() }
    override fun streamNameRoot(): String ="Stream1"

    //Test
    override fun given(): List<IDomainEvent> = listOf<GameEvent>(
        GameCreated.create(aggregateId),
        PlayerAdded.create(aggregateId, PLAYER_ID1),
        PlayerAdded.create(aggregateId, PLAYER_ID2),
        PlayerAdded.create(aggregateId, PLAYER_ID3),
        PlayerAdded.create(aggregateId, PLAYER_ID4),
    )

    override fun `when`(): ICommand<Game> = RequestDealCards(aggregateId, PLAYER_ID1)

    override fun expected(): List<IDomainEvent> = listOf(
        CardDealingRequested.create(aggregateId, PLAYER_ID1)
    )

    override fun expectedException(): Exception? = null
}

internal class Given_GameDraftWith3Player_When_RequestDealCards_Then_event : KcqrsAggregateTestSpecification<Game>(){
    companion object{
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID1=PlayerIdentity.create()
        val PLAYER_ID2=PlayerIdentity.create()
        val PLAYER_ID3=PlayerIdentity.create()
    }
    //Setup
    override val aggregateId: GameIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> GameDraft ={ GameDraft.empty() }
    override fun streamNameRoot(): String ="Stream1"

    //Test
    override fun given(): List<IDomainEvent> = listOf<GameEvent>(
        GameCreated.create(aggregateId),
        PlayerAdded.create(aggregateId, PLAYER_ID1),
        PlayerAdded.create(aggregateId, PLAYER_ID2),
        PlayerAdded.create(aggregateId, PLAYER_ID3),
    )

    override fun `when`(): ICommand<Game> = RequestDealCards(aggregateId, PLAYER_ID1)

    override fun expected(): List<IDomainEvent> = listOf(
        CardDealingRequested.create(aggregateId, PLAYER_ID1)
    )

    override fun expectedException(): Exception? = null
}

internal class Given_GameDraftWith1Player_When_RequestDealCards_Then_exception : KcqrsAggregateTestSpecification<Game>(){
    companion object{
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID1=PlayerIdentity.create()
    }
    //Setup
    override val aggregateId: GameIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> GameDraft ={ GameDraft.empty() }
    override fun streamNameRoot(): String ="Stream1"

    //Test
    override fun given(): List<IDomainEvent> = listOf<GameEvent>(
        GameCreated.create(aggregateId),
        PlayerAdded.create(aggregateId, PLAYER_ID1)
    )

    override fun `when`(): ICommand<Game> = RequestDealCards(aggregateId, PLAYER_ID1)

    override fun expected(): List<IDomainEvent> = listOf()

    override fun expectedException(): Exception? = IllegalStateException("Not enough players to deal the playing cards, ( Min players required: 2)")
}