package com.abaddon83.burraco.game.commands.gameExecutionPlayPhase

import com.abaddon83.burraco.game.events.game.*
import com.abaddon83.burraco.game.models.Straight
import com.abaddon83.burraco.common.models.StraightIdentity
import com.abaddon83.burraco.game.models.Tris
import com.abaddon83.burraco.common.models.TrisIdentity
import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.card.Ranks
import com.abaddon83.burraco.game.models.card.Suits
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


internal class Given_GameExecutionPlayPhase_When_RightPlayerDropValidTrisWithCardsNoBelongHim_Then_exception :
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

    override fun `when`(): ICommand<Game> = DropTris(
        aggregateId, PLAYER_ID1, TrisIdentity.create(), listOf(
            Card(Suits.Heart, Ranks.Six), Card(Suits.Heart, Ranks.Six), Card(Suits.Clover, Ranks.Six)
        )
    )

    override fun expected(): List<IDomainEvent> = listOf()

    override fun expectedException(): Exception? =
        IllegalArgumentException("Tris's cards don't belong to player ${PLAYER_ID1.valueAsString()}")
}

internal class Given_GameExecutionPlayPhase_When_RightPlayerDropValidTrisWithCards_Then_event :
    KcqrsAggregateTestSpecification<Game>() {
    companion object {
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID1 = PlayerIdentity.create()
        val PLAYER_ID2 = PlayerIdentity.create()
        val PLAYER_ID3 = PlayerIdentity.create()
        val PLAYER_ID4 = PlayerIdentity.create()
        val TRIS_ID = TrisIdentity.create()
        val TRIS_CARDS = listOf(Card(Suits.Heart, Ranks.Six), Card(Suits.Heart, Ranks.Six), Card(Suits.Clover, Ranks.Six))
        val gameDecksHelper: GameDecksHelper = DeckHelper.generateFakeDealerEvents(AGGREGATE_ID, listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3, PLAYER_ID4), PLAYER_ID1, TRIS_CARDS)
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

    override fun `when`(): ICommand<Game> = DropTris(aggregateId, PLAYER_ID1, TRIS_ID, TRIS_CARDS)

    override fun expected(): List<IDomainEvent> = listOf(
        TrisDropped.create(aggregateId, PLAYER_ID1, Tris.create(TRIS_ID, TRIS_CARDS))
    )

    override fun expectedException(): Exception? = null
}

internal class Given_GameExecutionPlayPhase_When_WrongPlayerDropValidTrisWithCards_Then_exception :
    KcqrsAggregateTestSpecification<Game>() {
    companion object {
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID1 = PlayerIdentity.create()
        val PLAYER_ID2 = PlayerIdentity.create()
        val PLAYER_ID3 = PlayerIdentity.create()
        val PLAYER_ID4 = PlayerIdentity.create()
        val TRIS_ID = TrisIdentity.create()
        val TRIS_CARDS = listOf(Card(Suits.Heart, Ranks.Six), Card(Suits.Heart, Ranks.Six), Card(Suits.Clover, Ranks.Six))
        val gameDecksHelper: GameDecksHelper = DeckHelper.generateFakeDealerEvents(AGGREGATE_ID, listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3, PLAYER_ID4), PLAYER_ID2, TRIS_CARDS)
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

    override fun `when`(): ICommand<Game> = DropTris(aggregateId, PLAYER_ID2, TRIS_ID, TRIS_CARDS)

    override fun expected(): List<IDomainEvent> = listOf()

    override fun expectedException(): Exception? = IllegalArgumentException("It's not the turn of the player ${PLAYER_ID2.valueAsString()}")
}

internal class Given_GameExecutionPlayPhase_When_RightPlayerWithNoBurracoDropTris_Then_event :
    KcqrsAggregateTestSpecification<Game>() {
    companion object {
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID1 = PlayerIdentity.create()
        val PLAYER_ID2 = PlayerIdentity.create()
        val PLAYER_ID3 = PlayerIdentity.create()
        val PLAYER_ID4 = PlayerIdentity.create()
        val TRIS_ID = TrisIdentity.create()
        val TRIS_CARDS = listOf(
            Card(Suits.Heart, Ranks.Queen),
            Card(Suits.Heart, Ranks.Queen),
            Card(Suits.Tile, Ranks.Queen),
            Card(Suits.Tile, Ranks.Queen),
            Card(Suits.Pike, Ranks.Queen)
        )
        val STRAIGHT_ID = StraightIdentity.create()
        val STRAIGHT_CARDS = listOf(
            Card(Suits.Heart, Ranks.Ace),
            Card(Suits.Heart, Ranks.Two),
            Card(Suits.Heart, Ranks.Three),
            Card(Suits.Heart, Ranks.Four),
            Card(Suits.Heart, Ranks.Five),
            Card(Suits.Heart, Ranks.Six)
        )
        val TRIS_ID2 = TrisIdentity.create()
        val TRIS_CARDS2 = listOf(
            Card(Suits.Heart, Ranks.King),
            Card(Suits.Heart, Ranks.King),
            Card(Suits.Tile, Ranks.King),
            Card(Suits.Tile, Ranks.King),
            Card(Suits.Pike, Ranks.King)
        )
        val STRAIGHT_ID2 = StraightIdentity.create()
        val STRAIGHT_CARDS2 = listOf(
            Card(Suits.Tile, Ranks.Ace),
            Card(Suits.Tile, Ranks.Two),
            Card(Suits.Tile, Ranks.Three),
            Card(Suits.Tile, Ranks.Four),
            Card(Suits.Tile, Ranks.Five),
            Card(Suits.Tile, Ranks.Six)
        )
        val PLAYER_DECK_1 = STRAIGHT_CARDS2.plus(TRIS_CARDS2)
        val DISCARD_DECK_CARD = Card(Suits.Clover, Ranks.Queen)
        val gameDecksHelper: GameDecksHelper = DeckHelper.generateFakeDealerEvents(AGGREGATE_ID, listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3, PLAYER_ID4), PLAYER_ID1, STRAIGHT_CARDS.plus(TRIS_CARDS), DISCARD_DECK_CARD,PLAYER_DECK_1)
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
            StraightDropped.create(aggregateId, PLAYER_ID1, Straight.create(STRAIGHT_ID, STRAIGHT_CARDS)),
            TrisDropped.create(aggregateId, PLAYER_ID1, Tris.create(TRIS_ID, TRIS_CARDS.plus(DISCARD_DECK_CARD))),
            CardsPickedFromPlayerDeckDuringTurn.create(aggregateId, PLAYER_ID1, gameDecksHelper.getCardsFromPlayerDeck1()),
            StraightDropped.create(aggregateId, PLAYER_ID1, Straight.create(STRAIGHT_ID2, STRAIGHT_CARDS2)),
        )
    )

    override fun `when`(): ICommand<Game> = DropTris(aggregateId, PLAYER_ID1, TRIS_ID2, TRIS_CARDS2)

    override fun expected(): List<IDomainEvent> = listOf()

    override fun expectedException(): Exception? = IllegalArgumentException("Not enough cards remaining on the player's hand")
}