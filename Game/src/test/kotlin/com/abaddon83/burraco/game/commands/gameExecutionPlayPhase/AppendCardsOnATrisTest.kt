package com.abaddon83.burraco.game.commands.gameExecutionPlayPhase

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.TrisIdentity
import com.abaddon83.burraco.common.models.card.Card
import com.abaddon83.burraco.common.models.card.Rank
import com.abaddon83.burraco.common.models.card.Suit
import com.abaddon83.burraco.common.models.event.game.CardDealingRequested
import com.abaddon83.burraco.common.models.event.game.CardsAddedToTris
import com.abaddon83.burraco.common.models.event.game.CardsPickedFromDiscardPile
import com.abaddon83.burraco.common.models.event.game.GameCreated
import com.abaddon83.burraco.common.models.event.game.GameEvent
import com.abaddon83.burraco.common.models.event.game.GameStarted
import com.abaddon83.burraco.common.models.event.game.PlayerAdded
import com.abaddon83.burraco.common.models.event.game.TrisDropped
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameDraft
import com.abaddon83.burraco.helper.DeckHelper
import com.abaddon83.burraco.helper.GameDecksHelper
import io.github.abaddon.kcqrs.core.IIdentity
import io.github.abaddon.kcqrs.core.domain.messages.commands.ICommand
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.github.abaddon.kcqrs.test.KcqrsAggregateTestSpecification


internal class Given_GameExecutionPlayPhase_When_RightPlayerAppenCardsOnAValidTrisWithCards_Then_event :
    KcqrsAggregateTestSpecification<Game>() {
    companion object {
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID1 = PlayerIdentity.create()
        val PLAYER_ID2 = PlayerIdentity.create()
        val PLAYER_ID3 = PlayerIdentity.create()
        val PLAYER_ID4 = PlayerIdentity.create()
        val TRIS_ID = TrisIdentity.create()
        val TRIS_CARDS = listOf(Card(Suit.Heart, Rank.Six), Card(Suit.Heart, Rank.Six), Card(Suit.Clover, Rank.Six))
        val APPEND_TRIS_CARDS = listOf(Card(Suit.Jolly, Rank.Jolly))
        val gameDecksHelper: GameDecksHelper = DeckHelper.generateFakeDealerEvents(
            AGGREGATE_ID, listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3, PLAYER_ID4), PLAYER_ID1, TRIS_CARDS.plus(
                APPEND_TRIS_CARDS
            )
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
            TrisDropped.create(aggregateId, PLAYER_ID1, TRIS_ID, TRIS_CARDS)
        )
    )

    override fun `when`(): ICommand<Game> = AppendCardsOnATris(aggregateId, PLAYER_ID1, TRIS_ID, APPEND_TRIS_CARDS)

    override fun expected(): List<IDomainEvent> = listOf(
        CardsAddedToTris.create(aggregateId, PLAYER_ID1, TRIS_ID, APPEND_TRIS_CARDS)
    )

    override fun expectedException(): Exception? = null
}

internal class Given_GameExecutionPlayPhase_When_RightPlayerAppenCardsOnAValidTrisWithNoCards_Then_exception :
    KcqrsAggregateTestSpecification<Game>() {
    companion object {
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID1 = PlayerIdentity.create()
        val PLAYER_ID2 = PlayerIdentity.create()
        val PLAYER_ID3 = PlayerIdentity.create()
        val PLAYER_ID4 = PlayerIdentity.create()
        val TRIS_ID = TrisIdentity.create()
        val TRIS_CARDS = listOf(Card(Suit.Heart, Rank.Six), Card(Suit.Heart, Rank.Six), Card(Suit.Clover, Rank.Six))
        val APPEND_TRIS_CARDS = listOf(Card(Suit.Jolly, Rank.Jolly))
        val gameDecksHelper: GameDecksHelper = DeckHelper.generateFakeDealerEvents(
            AGGREGATE_ID,
            listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3, PLAYER_ID4),
            PLAYER_ID1,
            TRIS_CARDS
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
            TrisDropped.create(aggregateId, PLAYER_ID1, TRIS_ID, TRIS_CARDS)
        )
    )

    override fun `when`(): ICommand<Game> = AppendCardsOnATris(aggregateId, PLAYER_ID1, TRIS_ID, APPEND_TRIS_CARDS)

    override fun expected(): List<IDomainEvent> = listOf()

    override fun expectedException(): Exception? =
        IllegalArgumentException("Cards to append to the tris ${TRIS_ID.valueAsString()} don't belong to player ${PLAYER_ID1.valueAsString()}")
}

internal class Given_GameExecutionPlayPhase_When_RightPlayerAppenCardsOnInValidTrisWithCards_Then_exception :
    KcqrsAggregateTestSpecification<Game>() {
    companion object {
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID1 = PlayerIdentity.create()
        val PLAYER_ID2 = PlayerIdentity.create()
        val PLAYER_ID3 = PlayerIdentity.create()
        val PLAYER_ID4 = PlayerIdentity.create()
        val TRIS_ID = TrisIdentity.create()
        val TRIS_CARDS = listOf(Card(Suit.Heart, Rank.Six), Card(Suit.Heart, Rank.Six), Card(Suit.Clover, Rank.Six))
        val APPEND_TRIS_CARDS = listOf(Card(Suit.Heart, Rank.Seven))
        val gameDecksHelper: GameDecksHelper = DeckHelper.generateFakeDealerEvents(
            AGGREGATE_ID,
            listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3, PLAYER_ID4),
            PLAYER_ID1,
            TRIS_CARDS
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
            TrisDropped.create(aggregateId, PLAYER_ID1, TRIS_ID, TRIS_CARDS)
        )
    )

    override fun `when`(): ICommand<Game> = AppendCardsOnATris(aggregateId, PLAYER_ID1, TRIS_ID, APPEND_TRIS_CARDS)

    override fun expected(): List<IDomainEvent> = listOf()

    override fun expectedException(): Exception? =
        IllegalArgumentException("Cards can't be added to Tris ${TRIS_ID.valueAsString()}")
}

internal class Given_GameExecutionPlayPhase_When_WrongPlayerAppenCardsOnAValidTrisWithCards_Then_exception :
    KcqrsAggregateTestSpecification<Game>() {
    companion object {
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID1 = PlayerIdentity.create()
        val PLAYER_ID2 = PlayerIdentity.create()
        val PLAYER_ID3 = PlayerIdentity.create()
        val PLAYER_ID4 = PlayerIdentity.create()
        val TRIS_ID = TrisIdentity.create()
        val TRIS_CARDS = listOf(Card(Suit.Heart, Rank.Six), Card(Suit.Heart, Rank.Six), Card(Suit.Clover, Rank.Six))
        val APPEND_TRIS_CARDS = listOf(Card(Suit.Jolly, Rank.Jolly))
        val gameDecksHelper: GameDecksHelper = DeckHelper.generateFakeDealerEvents(
            AGGREGATE_ID, listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3, PLAYER_ID4), PLAYER_ID2, TRIS_CARDS.plus(
                APPEND_TRIS_CARDS
            )
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
            TrisDropped.create(aggregateId, PLAYER_ID1, TRIS_ID, TRIS_CARDS)
        )
    )

    override fun `when`(): ICommand<Game> = AppendCardsOnATris(aggregateId, PLAYER_ID2, TRIS_ID, APPEND_TRIS_CARDS)

    override fun expected(): List<IDomainEvent> = listOf()

    override fun expectedException(): Exception? =
        IllegalArgumentException("It's not the turn of the player ${PLAYER_ID2.valueAsString()}")
}