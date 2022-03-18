package com.abaddon83.burraco.game.commands.gameWaitingDealer

import com.abaddon83.burraco.game.commands.gameExecutionPickUpPhase.Given_GameExecutionPickUpPhase_When_RightPlayerPickUpACardFromDeck_Then_event
import com.abaddon83.burraco.game.events.game.CardDealingRequested
import com.abaddon83.burraco.game.events.game.GameEvent
import com.abaddon83.burraco.game.events.game.GameStarted
import com.abaddon83.burraco.game.events.game.PlayerAdded
import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.card.Ranks
import com.abaddon83.burraco.game.models.card.Suits
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameDraft
import com.abaddon83.burraco.game.models.game.GameIdentity
import com.abaddon83.burraco.game.models.player.PlayerIdentity
import com.abaddon83.burraco.helpers.DeckHelper
import com.abaddon83.burraco.helpers.DeckHelper.generateFakeDealerEvents
import com.abaddon83.burraco.helpers.GameDecksHelper
import io.github.abaddon.kcqrs.core.IIdentity
import io.github.abaddon.kcqrs.core.domain.messages.commands.ICommand
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.github.abaddon.kcqrs.test.KcqrsAggregateTestSpecification

internal class Given_GameDraft_When_StartGame_Then_exception : KcqrsAggregateTestSpecification<Game>() {
    companion object {
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID1 = PlayerIdentity.create()
        val PLAYER_ID2 = PlayerIdentity.create()
        val CARD = Card(Suits.Clover, Ranks.Ace)
    }

    //Setup
    override val aggregateId: GameIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> GameDraft = { GameDraft.init(it as GameIdentity) }
    override fun streamNameRoot(): String = "Stream1"

    //Test
    override fun given(): List<IDomainEvent> = listOf<GameEvent>(
        PlayerAdded.create(aggregateId, PLAYER_ID1),
        PlayerAdded.create(aggregateId, PLAYER_ID2)
    )

    override fun `when`(): ICommand<Game> = StartGame(aggregateId)

    override fun expected(): List<IDomainEvent> = listOf()

    override fun expectedException(): Exception? = UnsupportedOperationException("Aggregate in a wrong status")
}

internal class Given_GameWaitingDealer_When_StartGame_Then_exception : KcqrsAggregateTestSpecification<Game>() {
    companion object {
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID1 = PlayerIdentity.create()
    }

    //Setup
    override val aggregateId: GameIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> GameDraft = { GameDraft.init(it as GameIdentity) }
    override fun streamNameRoot(): String = "Stream1"

    //Test
    override fun given(): List<IDomainEvent> = listOf<GameEvent>(
        PlayerAdded.create(aggregateId, PLAYER_ID1),
        PlayerAdded.create(aggregateId, PlayerIdentity.create()),
        CardDealingRequested.create(aggregateId, PLAYER_ID1),
    )

    override fun `when`(): ICommand<Game> = StartGame(aggregateId)

    override fun expected(): List<IDomainEvent> = listOf()

    override fun expectedException(): Exception? =
        IllegalStateException("The dealer has not finished dealing the cards")
}

internal class Given_GameWaitingDealerWithAllCardsDealt_When_StartGame_Then_event : KcqrsAggregateTestSpecification<Game>() {
    companion object {
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID1 = PlayerIdentity.create()
        val PLAYER_ID2 = PlayerIdentity.create()
        val PLAYER_ID3 = PlayerIdentity.create()
        val PLAYER_ID4 = PlayerIdentity.create()
        val gameDecksHelper: GameDecksHelper = generateFakeDealerEvents(AGGREGATE_ID, listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3, PLAYER_ID4))
    }

    //Setup
    override val aggregateId: GameIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> GameDraft = { GameDraft.init(it as GameIdentity) }
    override fun streamNameRoot(): String = "Stream1"

    //Test
    override fun given(): List<IDomainEvent> = listOf<GameEvent>(
        PlayerAdded.create(aggregateId, PLAYER_ID1),
        PlayerAdded.create(aggregateId, PLAYER_ID2),
        PlayerAdded.create(aggregateId, PLAYER_ID3),
        PlayerAdded.create(aggregateId, PLAYER_ID4),
        CardDealingRequested.create(aggregateId, PLAYER_ID1)
    ).plus(
        gameDecksHelper.events()
    )

    override fun `when`(): ICommand<Game> = StartGame(aggregateId)

    override fun expected(): List<IDomainEvent> = listOf(
        GameStarted.create(aggregateId)
    )

    override fun expectedException(): Exception? = null
}