package com.abaddon83.burraco.game.commands.gameExecutionPlayPhase

import com.abaddon83.burraco.game.events.game.*
import com.abaddon83.burraco.game.models.Straight
import com.abaddon83.burraco.game.models.StraightIdentity
import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.card.Ranks
import com.abaddon83.burraco.game.models.card.Suits
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameDraft
import com.abaddon83.burraco.game.models.game.GameIdentity
import com.abaddon83.burraco.game.models.player.PlayerIdentity
import com.abaddon83.burraco.helpers.DeckHelper
import com.abaddon83.burraco.helpers.GameDecksHelper
import io.github.abaddon.kcqrs.core.IIdentity
import io.github.abaddon.kcqrs.core.domain.messages.commands.ICommand
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.github.abaddon.kcqrs.test.KcqrsAggregateTestSpecification


internal class Given_GameExecutionPlayPhase_When_RightPlayerAddValidCardsToStraight_Then_event :
    KcqrsAggregateTestSpecification<Game>() {
    companion object {
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID1 = PlayerIdentity.create()
        val PLAYER_ID2 = PlayerIdentity.create()
        val PLAYER_ID3 = PlayerIdentity.create()
        val PLAYER_ID4 = PlayerIdentity.create()
        val STRAIGHT_ID = StraightIdentity.create()
        val STRAIGHT_CARDS = listOf(Card(Suits.Heart, Ranks.Three), Card(Suits.Heart, Ranks.Four), Card(Suits.Heart, Ranks.Five))
        val CARDS_TO_APPEND = listOf(Card(Suits.Heart, Ranks.Six))
        val gameDecksHelper: GameDecksHelper = DeckHelper.generateFakeDealerEvents(AGGREGATE_ID, listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3, PLAYER_ID4), PLAYER_ID1, STRAIGHT_CARDS.plus(CARDS_TO_APPEND))
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
    ).plus(
        listOf(
            GameStarted.create(aggregateId),
            CardsPickedFromDiscardPile.create(aggregateId, PLAYER_ID1, gameDecksHelper.getCardsFromDiscardDeck()),
            StraightDropped.create(aggregateId, PLAYER_ID1, Straight.create(STRAIGHT_ID, STRAIGHT_CARDS))
        )
    )

    override fun `when`(): ICommand<Game> = AppendCardsOnStraight(aggregateId, PLAYER_ID1, STRAIGHT_ID, CARDS_TO_APPEND)

    override fun expected(): List<IDomainEvent> = listOf(
        CardsAddedToStraight.create(aggregateId, PLAYER_ID1,STRAIGHT_ID,CARDS_TO_APPEND)
    )

    override fun expectedException(): Exception? = null
}

internal class Given_GameExecutionPlayPhase_When_RightPlayerAddValidCardsToStraightNoBelongHim_Then_exception :
    KcqrsAggregateTestSpecification<Game>() {
    companion object {
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID1 = PlayerIdentity.create()
        val PLAYER_ID2 = PlayerIdentity.create()
        val PLAYER_ID3 = PlayerIdentity.create()
        val PLAYER_ID4 = PlayerIdentity.create()
        val STRAIGHT_ID = StraightIdentity.create()
        val STRAIGHT_CARDS = listOf(Card(Suits.Tile, Ranks.Three), Card(Suits.Tile, Ranks.Four), Card(Suits.Tile, Ranks.Five))
        val gameDecksHelper: GameDecksHelper = DeckHelper.generateFakeDealerEvents(AGGREGATE_ID, listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3, PLAYER_ID4), PLAYER_ID1, STRAIGHT_CARDS)
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
    ).plus(
        listOf(
            GameStarted.create(aggregateId),
            CardsPickedFromDiscardPile.create(aggregateId, PLAYER_ID1, gameDecksHelper.getCardsFromDiscardDeck()),
            StraightDropped.create(aggregateId, PLAYER_ID1, Straight.create(STRAIGHT_ID, STRAIGHT_CARDS))
        )
    )

    override fun `when`(): ICommand<Game> = AppendCardsOnStraight(aggregateId, PLAYER_ID1, STRAIGHT_ID, listOf(Card(Suits.Tile, Ranks.Six)))

    override fun expected(): List<IDomainEvent> = listOf()

    override fun expectedException(): Exception? =
        IllegalArgumentException("Cards to append to the straight ${STRAIGHT_ID.valueAsString()} don't belong to player ${PLAYER_ID1.valueAsString()}")
}

internal class Given_GameExecutionPlayPhase_When_WrongPlayerAddValidCardsToStraight_Then_exception :
    KcqrsAggregateTestSpecification<Game>() {
    companion object {
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID1 = PlayerIdentity.create()
        val PLAYER_ID2 = PlayerIdentity.create()
        val PLAYER_ID3 = PlayerIdentity.create()
        val PLAYER_ID4 = PlayerIdentity.create()
        val STRAIGHT_ID = StraightIdentity.create()
        val STRAIGHT_CARDS = listOf(Card(Suits.Heart, Ranks.Three), Card(Suits.Heart, Ranks.Four), Card(Suits.Heart, Ranks.Five))
        val gameDecksHelper: GameDecksHelper = DeckHelper.generateFakeDealerEvents(AGGREGATE_ID, listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3, PLAYER_ID4), PLAYER_ID2, STRAIGHT_CARDS)
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
    ).plus(
        listOf(
            GameStarted.create(aggregateId),
            CardsPickedFromDiscardPile.create(aggregateId, PLAYER_ID1, gameDecksHelper.getCardsFromDiscardDeck()),
            StraightDropped.create(aggregateId, PLAYER_ID1, Straight.create(STRAIGHT_ID, STRAIGHT_CARDS))
        )
    )

    override fun `when`(): ICommand<Game> = AppendCardsOnStraight(aggregateId, PLAYER_ID2, STRAIGHT_ID, listOf(Card(Suits.Heart, Ranks.Six)))

    override fun expected(): List<IDomainEvent> = listOf()

    override fun expectedException(): Exception? = IllegalArgumentException("It's not the turn of the player ${PLAYER_ID2.valueAsString()}")
}