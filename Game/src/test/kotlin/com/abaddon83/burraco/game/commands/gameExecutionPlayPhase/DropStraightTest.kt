package com.abaddon83.burraco.game.commands.gameExecutionPlayPhase

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.StraightIdentity
import com.abaddon83.burraco.common.models.TrisIdentity
import com.abaddon83.burraco.common.models.card.Card
import com.abaddon83.burraco.common.models.card.Rank
import com.abaddon83.burraco.common.models.card.Suit
import com.abaddon83.burraco.common.models.event.game.CardDealingRequested
import com.abaddon83.burraco.common.models.event.game.CardsPickedFromDiscardPile
import com.abaddon83.burraco.common.models.event.game.CardsPickedFromPlayerDeckDuringTurn
import com.abaddon83.burraco.common.models.event.game.GameCreated
import com.abaddon83.burraco.common.models.event.game.GameEvent
import com.abaddon83.burraco.common.models.event.game.GameStarted
import com.abaddon83.burraco.common.models.event.game.PlayerAdded
import com.abaddon83.burraco.common.models.event.game.StraightDropped
import com.abaddon83.burraco.common.models.event.game.TrisDropped
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameDraft
import com.abaddon83.burraco.helper.DeckHelper
import com.abaddon83.burraco.helper.GameDecksHelper
import io.github.abaddon.kcqrs.core.IIdentity
import io.github.abaddon.kcqrs.core.domain.messages.commands.ICommand
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.github.abaddon.kcqrs.test.KcqrsAggregateTestSpecification


internal class Given_GameExecutionPlayPhase_When_RightPlayerDropValidStraightWithCards_Then_event :
    KcqrsAggregateTestSpecification<Game>() {
    companion object {
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID1 = PlayerIdentity.create()
        val PLAYER_ID2 = PlayerIdentity.create()
        val PLAYER_ID3 = PlayerIdentity.create()
        val PLAYER_ID4 = PlayerIdentity.create()
        val STRAIGHT_ID = StraightIdentity.create()
        val STRAIGHT_CARDS =
            listOf(Card(Suit.Heart, Rank.Three), Card(Suit.Heart, Rank.Four), Card(Suit.Heart, Rank.Five))
        val gameDecksHelper: GameDecksHelper = DeckHelper.generateFakeDealerEvents(
            AGGREGATE_ID,
            listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3, PLAYER_ID4),
            PLAYER_ID1,
            STRAIGHT_CARDS
        )
    }

    //Setup
    override val aggregateId: GameIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> GameDraft = { id -> GameDraft.empty() }
    override fun streamNameRoot(): String = "Stream1"
    override fun membersToIgnore(): List<String> = listOf("header")

    //Test
    override fun given(): List<IDomainEvent> = listOf<GameEvent>(
        GameCreated.create(aggregateId),
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
            CardsPickedFromDiscardPile.create(aggregateId, PLAYER_ID1, gameDecksHelper.getCardsFromDiscardDeck())
        )
    )

    override fun `when`(): ICommand<Game> = DropStraight(aggregateId, PLAYER_ID1, STRAIGHT_ID, STRAIGHT_CARDS)

    override fun expected(): List<IDomainEvent> = listOf(
        StraightDropped.create(aggregateId, PLAYER_ID1, STRAIGHT_ID, STRAIGHT_CARDS.sortedByDescending { it.rank.position })
    )

    override fun expectedException(): Exception? = null
}

internal class Given_GameExecutionPlayPhase_When_RightPlayerDropValidStraightWithCardsNoBelongHim_Then_exception :
    KcqrsAggregateTestSpecification<Game>() {
    companion object {
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID1 = PlayerIdentity.create()
        val PLAYER_ID2 = PlayerIdentity.create()
        val PLAYER_ID3 = PlayerIdentity.create()
        val PLAYER_ID4 = PlayerIdentity.create()
        val gameDecksHelper: GameDecksHelper =
            DeckHelper.generateFakeDealerEvents(AGGREGATE_ID, listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3, PLAYER_ID4))
    }

    //Setup
    override val aggregateId: GameIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> GameDraft = { id -> GameDraft.empty() }
    override fun streamNameRoot(): String = "Stream1"
    override fun membersToIgnore(): List<String> = listOf("header")

    //Test
    override fun given(): List<IDomainEvent> = listOf<GameEvent>(
        GameCreated.create(aggregateId),
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
            CardsPickedFromDiscardPile.create(aggregateId, PLAYER_ID1, gameDecksHelper.getCardsFromDiscardDeck())
        )
    )

    override fun `when`(): ICommand<Game> = DropStraight(
        aggregateId,
        PLAYER_ID1,
        StraightIdentity.create(),
        listOf(Card(Suit.Tile, Rank.Three), Card(Suit.Tile, Rank.Four), Card(Suit.Tile, Rank.Five))
    )

    override fun expected(): List<IDomainEvent> = listOf()

    override fun expectedException(): Exception? =
        IllegalArgumentException("Straight's cards don't belong to player ${PLAYER_ID1.valueAsString()}")
}

internal class Given_GameExecutionPlayPhase_When_WrongPlayerDropValidStraightWithCards_Then_exception :
    KcqrsAggregateTestSpecification<Game>() {
    companion object {
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID1 = PlayerIdentity.create()
        val PLAYER_ID2 = PlayerIdentity.create()
        val PLAYER_ID3 = PlayerIdentity.create()
        val PLAYER_ID4 = PlayerIdentity.create()
        val STRAIGHT_ID = StraightIdentity.create()
        val STRAIGHT_CARDS =
            listOf(Card(Suit.Heart, Rank.Three), Card(Suit.Heart, Rank.Four), Card(Suit.Heart, Rank.Five))
        val gameDecksHelper: GameDecksHelper = DeckHelper.generateFakeDealerEvents(
            AGGREGATE_ID,
            listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3, PLAYER_ID4),
            PLAYER_ID2,
            STRAIGHT_CARDS
        )
    }

    //Setup
    override val aggregateId: GameIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> GameDraft = { id -> GameDraft.empty() }
    override fun streamNameRoot(): String = "Stream1"
    override fun membersToIgnore(): List<String> = listOf("header")

    //Test
    override fun given(): List<IDomainEvent> = listOf<GameEvent>(
        GameCreated.create(aggregateId),
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
            CardsPickedFromDiscardPile.create(aggregateId, PLAYER_ID1, gameDecksHelper.getCardsFromDiscardDeck())
        )
    )

    override fun `when`(): ICommand<Game> = DropStraight(aggregateId, PLAYER_ID2, STRAIGHT_ID, STRAIGHT_CARDS)

    override fun expected(): List<IDomainEvent> = listOf()

    override fun expectedException(): Exception? =
        IllegalArgumentException("It's not the turn of the player ${PLAYER_ID2.valueAsString()}")
}

internal class Given_GameExecutionPlayPhase_When_RightPlayerWithNoBurracoDropStraigh_Then_event :
    KcqrsAggregateTestSpecification<Game>() {
    companion object {
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID1 = PlayerIdentity.create()
        val PLAYER_ID2 = PlayerIdentity.create()
        val PLAYER_ID3 = PlayerIdentity.create()
        val PLAYER_ID4 = PlayerIdentity.create()
        val TRIS_ID = TrisIdentity.create()
        val TRIS_CARDS = listOf(
            Card(Suit.Heart, Rank.Queen),
            Card(Suit.Heart, Rank.Queen),
            Card(Suit.Tile, Rank.Queen),
            Card(Suit.Tile, Rank.Queen),
            Card(Suit.Pike, Rank.Queen)
        )
        val STRAIGHT_ID = StraightIdentity.create()
        val STRAIGHT_CARDS = listOf(
            Card(Suit.Heart, Rank.Ace),
            Card(Suit.Heart, Rank.Two),
            Card(Suit.Heart, Rank.Three),
            Card(Suit.Heart, Rank.Four),
            Card(Suit.Heart, Rank.Five),
            Card(Suit.Heart, Rank.Six)
        )
        val TRIS_ID2 = TrisIdentity.create()
        val TRIS_CARDS2 = listOf(
            Card(Suit.Heart, Rank.King),
            Card(Suit.Heart, Rank.King),
            Card(Suit.Tile, Rank.King),
            Card(Suit.Tile, Rank.King),
            Card(Suit.Pike, Rank.King)
        )
        val STRAIGHT_ID2 = StraightIdentity.create()
        val STRAIGHT_CARDS2 = listOf(
            Card(Suit.Tile, Rank.Ace),
            Card(Suit.Tile, Rank.Two),
            Card(Suit.Tile, Rank.Three),
            Card(Suit.Tile, Rank.Four),
            Card(Suit.Tile, Rank.Five),
            Card(Suit.Tile, Rank.Six)
        )
        val PLAYER_DECK_1 = STRAIGHT_CARDS2.plus(TRIS_CARDS2)
        val DISCARD_DECK_CARD = Card(Suit.Clover, Rank.Queen)
        val gameDecksHelper: GameDecksHelper = DeckHelper.generateFakeDealerEvents(
            AGGREGATE_ID,
            listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3, PLAYER_ID4),
            PLAYER_ID1,
            STRAIGHT_CARDS.plus(TRIS_CARDS),
            DISCARD_DECK_CARD,
            PLAYER_DECK_1
        )
    }

    //Setup
    override val aggregateId: GameIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> GameDraft = { id -> GameDraft.empty() }
    override fun streamNameRoot(): String = "Stream1"
    override fun membersToIgnore(): List<String> = listOf("header")

    //Test
    override fun given(): List<IDomainEvent> = listOf<GameEvent>(
        GameCreated.create(aggregateId),
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
            StraightDropped.create(aggregateId, PLAYER_ID1, STRAIGHT_ID, STRAIGHT_CARDS),
            TrisDropped.create(aggregateId, PLAYER_ID1, TRIS_ID, TRIS_CARDS.plus(DISCARD_DECK_CARD)),
            CardsPickedFromPlayerDeckDuringTurn.create(
                aggregateId,
                PLAYER_ID1,
                gameDecksHelper.getCardsFromPlayerDeck1()
            ),
            TrisDropped.create(aggregateId, PLAYER_ID1, TRIS_ID2, TRIS_CARDS2)
        )
    )

    override fun `when`(): ICommand<Game> = DropStraight(aggregateId, PLAYER_ID1, STRAIGHT_ID2, STRAIGHT_CARDS2)

    override fun expected(): List<IDomainEvent> = listOf()

    override fun expectedException(): Exception? =
        IllegalArgumentException("Not enough cards remaining on the player's hand")
}