package com.abaddon83.burraco.game.commands.gameDraft

import com.abaddon83.burraco.game.events.game.GameCreated
import com.abaddon83.burraco.game.events.game.GameEvent
import com.abaddon83.burraco.game.events.game.PlayerAdded
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameDraft
import com.abaddon83.burraco.game.models.game.GameIdentity
import com.abaddon83.burraco.game.models.player.PlayerIdentity
import io.github.abaddon.kcqrs.core.IIdentity
import io.github.abaddon.kcqrs.core.domain.messages.commands.ICommand
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.github.abaddon.kcqrs.test.KcqrsAggregateTestSpecification

internal class Given_an_emptyGame_When_AddPlayer_Then_event : KcqrsAggregateTestSpecification<Game>(){
    companion object{
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID=PlayerIdentity.create()
    }
    //Setup
    override val aggregateId: GameIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> GameDraft ={ GameDraft.empty() }
    override fun streamNameRoot(): String ="Stream1"

    //Test
    override fun given(): List<IDomainEvent> = listOf<GameEvent>(
        GameCreated.create(aggregateId)
    )

    override fun `when`(): ICommand<Game> = AddPlayer(aggregateId, PLAYER_ID)

    override fun expected(): List<IDomainEvent> = listOf(
        PlayerAdded.create(aggregateId, PLAYER_ID)
    )

    override fun expectedException(): Exception? = null
}

internal class Given_GameDraftWith1Player_When_AddSamePlayer_Then_exception : KcqrsAggregateTestSpecification<Game>(){
    companion object{
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID=PlayerIdentity.create()
    }
    //Setup
    override val aggregateId: GameIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> GameDraft ={ GameDraft.empty() }
    override fun streamNameRoot(): String ="Stream1"

    //Test
    override fun given(): List<IDomainEvent> = listOf<GameEvent>(
        GameCreated.create(aggregateId),
        PlayerAdded.create(aggregateId, PLAYER_ID)
    )

    override fun `when`(): ICommand<Game> = AddPlayer(aggregateId, PLAYER_ID)

    override fun expected(): List<IDomainEvent> = listOf(
        PlayerAdded.create(aggregateId, PLAYER_ID)
    )

    override fun expectedException(): Exception? {
        return IllegalArgumentException("The player ${PLAYER_ID.valueAsString()} is already a player of game ${AGGREGATE_ID.valueAsString()}")
    }
}

internal class Given_GameDraftWith3Player_When_AddNewPlayer_Then_event : KcqrsAggregateTestSpecification<Game>(){
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
    )

    override fun `when`(): ICommand<Game> = AddPlayer(aggregateId, PLAYER_ID4)

    override fun expected(): List<IDomainEvent> = listOf(
        PlayerAdded.create(aggregateId, PLAYER_ID4)
    )

    override fun expectedException(): Exception? = null
}

internal class Given_GameDraftWith4Player_When_AddNewPlayer_Then_exception : KcqrsAggregateTestSpecification<Game>(){
    companion object{
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID1=PlayerIdentity.create()
        val PLAYER_ID2=PlayerIdentity.create()
        val PLAYER_ID3=PlayerIdentity.create()
        val PLAYER_ID4=PlayerIdentity.create()
        val PLAYER_ID5=PlayerIdentity.create()
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

    override fun `when`(): ICommand<Game> = AddPlayer(aggregateId, PLAYER_ID5)

    override fun expected(): List<IDomainEvent> = listOf()

    override fun expectedException(): Exception? = IllegalStateException("Maximum number of players reached, (Max: 4)")
}