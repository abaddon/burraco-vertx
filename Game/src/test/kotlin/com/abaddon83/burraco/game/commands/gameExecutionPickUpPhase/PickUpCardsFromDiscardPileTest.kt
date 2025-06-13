package com.abaddon83.burraco.game.commands.gameExecutionPickUpPhase

import com.abaddon83.burraco.game.events.game.*
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameDraft
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.helpers.DeckHelper
import com.abaddon83.burraco.helpers.GameDecksHelper
import io.github.abaddon.kcqrs.core.IIdentity
import io.github.abaddon.kcqrs.core.domain.messages.commands.ICommand
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.github.abaddon.kcqrs.test.KcqrsAggregateTestSpecification


internal class Given_GameExecutionPickUpPhase_When_RightPlayerPickUpCardsFromDiscardPile_Then_event : KcqrsAggregateTestSpecification<Game>(){
    companion object{
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID1= PlayerIdentity.create()
        val PLAYER_ID2= PlayerIdentity.create()
        val PLAYER_ID3= PlayerIdentity.create()
        val PLAYER_ID4= PlayerIdentity.create()
        val gameDecksHelper: GameDecksHelper = DeckHelper.generateFakeDealerEvents(AGGREGATE_ID, listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3, PLAYER_ID4))
    }
    //Setup
    override val aggregateId: GameIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> GameDraft = { id -> GameDraft.empty() }
    override fun streamNameRoot(): String ="Stream1"
    override fun membersToIgnore(): List<String> = listOf("header")

    //Test
    override fun given(): List<IDomainEvent> = listOf<GameEvent>(
        GameCreated.create(aggregateId),
        PlayerAdded.create(aggregateId,PLAYER_ID1),
        PlayerAdded.create(aggregateId,PLAYER_ID2),
        PlayerAdded.create(aggregateId,PLAYER_ID3),
        PlayerAdded.create(aggregateId,PLAYER_ID4),
        CardDealingRequested.create(aggregateId, PLAYER_ID1)
    ).plus(
        gameDecksHelper.events()
    ).plus(listOf(
        GameStarted.create(aggregateId)
    ))

    override fun `when`(): ICommand<Game> = PickUpCardsFromDiscardPile(aggregateId, PLAYER_ID1)

    override fun expected(): List<IDomainEvent> = listOf(
        CardsPickedFromDiscardPile.create(aggregateId, PLAYER_ID1,gameDecksHelper.getCardsFromDiscardDeck())
    )

    override fun expectedException(): Exception? = null
}

internal class Given_GameExecutionPickUpPhase_When_WrongPlayerPickUpCardsFromDiscardPile_Then_event : KcqrsAggregateTestSpecification<Game>(){
    companion object{
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID1= PlayerIdentity.create()
        val PLAYER_ID2= PlayerIdentity.create()
        val PLAYER_ID3= PlayerIdentity.create()
        val PLAYER_ID4= PlayerIdentity.create()
        val gameDecksHelper: GameDecksHelper = DeckHelper.generateFakeDealerEvents(AGGREGATE_ID, listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3, PLAYER_ID4))
    }
    //Setup
    override val aggregateId: GameIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> GameDraft = { id -> GameDraft.empty() }
    override fun streamNameRoot(): String ="Stream1"
    override fun membersToIgnore(): List<String> = listOf("header")

    //Test
    override fun given(): List<IDomainEvent> = listOf<GameEvent>(
        GameCreated.create(aggregateId),
        PlayerAdded.create(aggregateId,PLAYER_ID1),
        PlayerAdded.create(aggregateId,PLAYER_ID2),
        PlayerAdded.create(aggregateId,PLAYER_ID3),
        PlayerAdded.create(aggregateId,PLAYER_ID4),
        CardDealingRequested.create(aggregateId, PLAYER_ID1)
    ).plus(
        gameDecksHelper.events()
    ).plus(listOf(
        GameStarted.create(aggregateId)
    ))

    override fun `when`(): ICommand<Game> = PickUpCardsFromDiscardPile(aggregateId, PLAYER_ID2)

    override fun expected(): List<IDomainEvent> = listOf()

    override fun expectedException(): Exception? = IllegalStateException("It's not the turn of the player ${PLAYER_ID2.valueAsString()}")
}

internal class Given_GameExecutionPickUpPhase_When_NoValidPlayerPickUpCardsFromDiscardPile_Then_event : KcqrsAggregateTestSpecification<Game>(){
    companion object{
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID1= PlayerIdentity.create()
        val PLAYER_ID2= PlayerIdentity.create()
        val PLAYER_ID3= PlayerIdentity.create()
        val PLAYER_ID4= PlayerIdentity.create()
        val PLAYER_OUTSIDE_GAME= PlayerIdentity.create()
        val gameDecksHelper: GameDecksHelper = DeckHelper.generateFakeDealerEvents(AGGREGATE_ID, listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3, PLAYER_ID4))
    }
    //Setup
    override val aggregateId: GameIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> GameDraft = { id -> GameDraft.empty() }
    override fun streamNameRoot(): String ="Stream1"
    override fun membersToIgnore(): List<String> = listOf("header")

    //Test
    override fun given(): List<IDomainEvent> = listOf<GameEvent>(
        GameCreated.create(aggregateId),
        PlayerAdded.create(aggregateId,PLAYER_ID1),
        PlayerAdded.create(aggregateId,PLAYER_ID2),
        PlayerAdded.create(aggregateId,PLAYER_ID3),
        PlayerAdded.create(aggregateId,PLAYER_ID4),
        CardDealingRequested.create(aggregateId, PLAYER_ID1)
    ).plus(
        gameDecksHelper.events()
    ).plus(listOf(
        GameStarted.create(aggregateId)
    ))

    override fun `when`(): ICommand<Game> = PickUpCardsFromDiscardPile(aggregateId, PLAYER_OUTSIDE_GAME)

    override fun expected(): List<IDomainEvent> = listOf()

    override fun expectedException(): Exception? = IllegalStateException("Player ${PLAYER_OUTSIDE_GAME.valueAsString()} is not a player of the game ${AGGREGATE_ID.valueAsString()}")
}