package com.abaddon83.burraco.game.models.game

import com.abaddon83.burraco.game.commands.AddPlayer
import com.abaddon83.burraco.game.events.game.GameEvent
import com.abaddon83.burraco.game.events.game.PlayerAdded
import com.abaddon83.burraco.game.models.player.PlayerIdentity
import io.github.abaddon.kcqrs.core.IIdentity
import io.github.abaddon.kcqrs.core.domain.messages.commands.ICommand
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.github.abaddon.kcqrs.test.KcqrsAggregateTestSpecification

internal class `Given a new game when a new player added then GameDraft created with a player` : KcqrsAggregateTestSpecification<Game>(){
    companion object{
        val AGGREGATE_ID =GameIdentity.create()
        val PLAYER_ID=PlayerIdentity.create()
    }
    //Setup
    override val aggregateId: GameIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> GameDraft ={ GameDraft.init(it as GameIdentity) }
    override fun streamNameRoot(): String ="Stream1"

    //Test
    override fun given(): List<IDomainEvent> = listOf<GameEvent>()

    override fun `when`(): ICommand<Game> =AddPlayer(aggregateId,PLAYER_ID)

    override fun expected(): List<IDomainEvent> = listOf(
        PlayerAdded.create(aggregateId,PLAYER_ID)
    )

    override fun expectedException(): Exception? = null
}

internal class `Given a game with a player when add the same player then exception raised` : KcqrsAggregateTestSpecification<Game>(){
    companion object{
        val AGGREGATE_ID =GameIdentity.create()
        val PLAYER_ID=PlayerIdentity.create()
    }
    //Setup
    override val aggregateId: GameIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> GameDraft ={ GameDraft.init(it as GameIdentity) }
    override fun streamNameRoot(): String ="Stream1"

    //Test
    override fun given(): List<IDomainEvent> = listOf<GameEvent>(
        PlayerAdded.create(aggregateId,PLAYER_ID)
    )

    override fun `when`(): ICommand<Game> =AddPlayer(aggregateId,PLAYER_ID)

    override fun expected(): List<IDomainEvent> = listOf(
        PlayerAdded.create(aggregateId,PLAYER_ID)
    )

    override fun expectedException(): Exception? {
        return IllegalArgumentException("The player ${PLAYER_ID.valueAsString()} is already a player of game ${AGGREGATE_ID.valueAsString()}")
    }
}

internal class `Given a game with 3 players when add a new player then GameDraft hass 4 players` : KcqrsAggregateTestSpecification<Game>(){
    companion object{
        val AGGREGATE_ID =GameIdentity.create()
        val PLAYER_ID1=PlayerIdentity.create()
        val PLAYER_ID2=PlayerIdentity.create()
        val PLAYER_ID3=PlayerIdentity.create()
        val PLAYER_ID4=PlayerIdentity.create()
    }
    //Setup
    override val aggregateId: GameIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> GameDraft ={ GameDraft.init(it as GameIdentity) }
    override fun streamNameRoot(): String ="Stream1"

    //Test
    override fun given(): List<IDomainEvent> = listOf<GameEvent>(
        PlayerAdded.create(aggregateId,PLAYER_ID1),
        PlayerAdded.create(aggregateId,PLAYER_ID2),
        PlayerAdded.create(aggregateId,PLAYER_ID3),
    )

    override fun `when`(): ICommand<Game> =AddPlayer(aggregateId,PLAYER_ID4)

    override fun expected(): List<IDomainEvent> = listOf(
        PlayerAdded.create(aggregateId,PLAYER_ID4)
    )

    override fun expectedException(): Exception? = null
}

internal class `Given a game with 4 players when add a new player then an exception is raised` : KcqrsAggregateTestSpecification<Game>(){
    companion object{
        val AGGREGATE_ID =GameIdentity.create()
        val PLAYER_ID1=PlayerIdentity.create()
        val PLAYER_ID2=PlayerIdentity.create()
        val PLAYER_ID3=PlayerIdentity.create()
        val PLAYER_ID4=PlayerIdentity.create()
        val PLAYER_ID5=PlayerIdentity.create()
    }
    //Setup
    override val aggregateId: GameIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> GameDraft ={ GameDraft.init(it as GameIdentity) }
    override fun streamNameRoot(): String ="Stream1"

    //Test
    override fun given(): List<IDomainEvent> = listOf<GameEvent>(
        PlayerAdded.create(aggregateId,PLAYER_ID1),
        PlayerAdded.create(aggregateId,PLAYER_ID2),
        PlayerAdded.create(aggregateId,PLAYER_ID3),
        PlayerAdded.create(aggregateId,PLAYER_ID4),
    )

    override fun `when`(): ICommand<Game> =AddPlayer(aggregateId,PLAYER_ID5)

    override fun expected(): List<IDomainEvent> = listOf()

    override fun expectedException(): Exception? = IllegalStateException("Maximum number of players reached, (Max: 4)")
}