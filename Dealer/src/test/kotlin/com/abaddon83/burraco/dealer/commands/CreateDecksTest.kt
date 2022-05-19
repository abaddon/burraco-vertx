package com.abaddon83.burraco.dealer.commands

import com.abaddon83.burraco.dealer.events.DealerEvent
import com.abaddon83.burraco.dealer.events.DeckCreated
import com.abaddon83.burraco.dealer.models.Dealer
import com.abaddon83.burraco.dealer.models.DealerIdentity
import com.abaddon83.burraco.dealer.models.GameIdentity
import com.abaddon83.burraco.dealer.models.PlayerIdentity
import io.github.abaddon.kcqrs.core.IIdentity
import io.github.abaddon.kcqrs.core.domain.messages.commands.ICommand
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.github.abaddon.kcqrs.test.KcqrsAggregateTestSpecification
import org.junit.jupiter.api.Assertions.*

internal class Given_Nothing_When_CreatDeck_Then_event : KcqrsAggregateTestSpecification<Dealer>(){
    companion object{
        val AGGREGATE_ID = DealerIdentity.create()
        val PLAYER_ID1= PlayerIdentity.create()
        val PLAYER_ID2= PlayerIdentity.create()
        val PLAYER_ID3= PlayerIdentity.create()
        val PLAYERS = listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3)
        val GAME_ID = GameIdentity.create()

    }
    //Setup
    override val aggregateId: DealerIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> Dealer ={ Dealer.empty() }
    override fun streamNameRoot(): String ="Stream1"

    //Test
    override fun given(): List<IDomainEvent> = listOf<DealerEvent>()

    override fun `when`(): ICommand<Dealer> = CreateDecks(aggregateId, gameIdentity = GAME_ID, players = PLAYERS)

    override fun expected(): List<IDomainEvent> = listOf(
        DeckCreated.create(AGGREGATE_ID, GAME_ID, PLAYERS, listOf())
    )

    override fun expectedException(): Exception? = null
}