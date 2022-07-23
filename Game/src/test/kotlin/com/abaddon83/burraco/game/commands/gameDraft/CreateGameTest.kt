package com.abaddon83.burraco.game.commands.gameDraft

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

internal class Given_Nothing_When_CreateGame_Then_event : KcqrsAggregateTestSpecification<Game>(){
    companion object{
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID=PlayerIdentity.create()
    }
    //Setup
    override val aggregateId: GameIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> GameDraft ={ GameDraft.empty() }
    override fun streamNameRoot(): String ="Stream1"

    //Test
    override fun given(): List<IDomainEvent> = listOf<GameEvent>()

    override fun `when`(): ICommand<Game> = CreateGame(aggregateId)

    override fun expected(): List<IDomainEvent> = listOf(
        GameCreated.create(aggregateId)
    )

    override fun expectedException(): Exception? = null
}