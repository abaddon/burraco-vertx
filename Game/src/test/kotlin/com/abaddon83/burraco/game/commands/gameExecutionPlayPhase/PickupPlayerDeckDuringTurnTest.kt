package com.abaddon83.burraco.game.commands.gameExecutionPlayPhase

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.StraightIdentity
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
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameDraft
import com.abaddon83.burraco.helper.DeckHelper
import com.abaddon83.burraco.helper.GameDecksHelper
import io.github.abaddon.kcqrs.core.IIdentity
import io.github.abaddon.kcqrs.core.domain.messages.commands.ICommand
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.github.abaddon.kcqrs.test.KcqrsAggregateTestSpecification


internal class Given_GameExecutionPlayPhase_When_RightPlayerWithNoCardsPickUpPlayerDeck_Then_event :
    KcqrsAggregateTestSpecification<Game>() {
    companion object {
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID1 = PlayerIdentity.create()
        val PLAYER_ID2 = PlayerIdentity.create()
        val PLAYER_ID3 = PlayerIdentity.create()
        val PLAYER_ID4 = PlayerIdentity.create()
        val STRAIGHT_ID = StraightIdentity.create()
        val STRAIGHT_CARDS = listOf(
            Card(Suit.Heart, Rank.Ace),
            Card(Suit.Heart, Rank.Two),
            Card(Suit.Heart, Rank.Three),
            Card(Suit.Heart, Rank.Four),
            Card(Suit.Heart, Rank.Five),
            Card(Suit.Heart, Rank.Six),
            Card(Suit.Heart, Rank.Seven),
            Card(Suit.Heart, Rank.Eight),
            Card(Suit.Heart, Rank.Nine),
            Card(Suit.Heart, Rank.Ten),
            Card(Suit.Heart, Rank.Jack)
        )
        val DISCARD_DECK_CARD = Card(Suit.Heart, Rank.Queen)
        val gameDecksHelper: GameDecksHelper = DeckHelper.generateFakeDealerEvents(
            AGGREGATE_ID,
            listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3, PLAYER_ID4),
            PLAYER_ID1,
            STRAIGHT_CARDS,
            DISCARD_DECK_CARD
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
            StraightDropped.create(aggregateId, PLAYER_ID1, STRAIGHT_ID, STRAIGHT_CARDS.plus(DISCARD_DECK_CARD))
        )
    )

    override fun `when`(): ICommand<Game> = PickUpPlayerDeckDuringTurn(aggregateId, PLAYER_ID1)

    override fun expected(): List<IDomainEvent> = listOf(
        CardsPickedFromPlayerDeckDuringTurn.create(aggregateId, PLAYER_ID1, gameDecksHelper.getCardsFromPlayerDeck1())
    )

    override fun expectedException(): Exception? = null
}

internal class Given_GameExecutionPlayPhase_When_RightPlayerWithCardsPickUpPlayerDeck_Then_exception :
    KcqrsAggregateTestSpecification<Game>() {
    companion object {
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID1 = PlayerIdentity.create()
        val PLAYER_ID2 = PlayerIdentity.create()
        val PLAYER_ID3 = PlayerIdentity.create()
        val PLAYER_ID4 = PlayerIdentity.create()
        val STRAIGHT_ID = StraightIdentity.create()
        val STRAIGHT_CARDS = listOf(
            Card(Suit.Heart, Rank.Ace),
            Card(Suit.Heart, Rank.Two),
            Card(Suit.Heart, Rank.Three),
            Card(Suit.Heart, Rank.Four),
            Card(Suit.Heart, Rank.Five),
            Card(Suit.Heart, Rank.Six),
            Card(Suit.Heart, Rank.Seven),
            Card(Suit.Heart, Rank.Eight),
            Card(Suit.Heart, Rank.Nine),
            Card(Suit.Heart, Rank.Ten),
            Card(Suit.Heart, Rank.Jack)
        )
        val DISCARD_DECK_CARD = Card(Suit.Heart, Rank.Queen)
        val gameDecksHelper: GameDecksHelper = DeckHelper.generateFakeDealerEvents(
            AGGREGATE_ID,
            listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3, PLAYER_ID4),
            PLAYER_ID1,
            STRAIGHT_CARDS,
            DISCARD_DECK_CARD
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
            StraightDropped.create(aggregateId, PLAYER_ID1, STRAIGHT_ID, STRAIGHT_CARDS)
        )
    )

    override fun `when`(): ICommand<Game> = PickUpPlayerDeckDuringTurn(aggregateId, PLAYER_ID1)

    override fun expected(): List<IDomainEvent> = listOf()

    override fun expectedException(): Exception? =
        IllegalArgumentException("Player ${PLAYER_ID1.valueAsString()} still has cards in their hand")
}

internal class Given_GameExecutionPlayPhase_When_RightPlayerWithNoCardsPickUpPlayerDeck2Times_Then_event :
    KcqrsAggregateTestSpecification<Game>() {
    companion object {
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID1 = PlayerIdentity.create()
        val PLAYER_ID2 = PlayerIdentity.create()
        val PLAYER_ID3 = PlayerIdentity.create()
        val PLAYER_ID4 = PlayerIdentity.create()
        val STRAIGHT_ID = StraightIdentity.create()
        val STRAIGHT_CARDS = listOf(
            Card(Suit.Heart, Rank.Ace),
            Card(Suit.Heart, Rank.Two),
            Card(Suit.Heart, Rank.Three),
            Card(Suit.Heart, Rank.Four),
            Card(Suit.Heart, Rank.Five),
            Card(Suit.Heart, Rank.Six),
            Card(Suit.Heart, Rank.Seven),
            Card(Suit.Heart, Rank.Eight),
            Card(Suit.Heart, Rank.Nine),
            Card(Suit.Heart, Rank.Ten),
            Card(Suit.Heart, Rank.Jack)
        )
        val DISCARD_DECK_CARD = Card(Suit.Heart, Rank.Queen)
        val STRAIGHT_CARDS2 = listOf(
            Card(Suit.Pike, Rank.Ace),
            Card(Suit.Pike, Rank.Two),
            Card(Suit.Pike, Rank.Three),
            Card(Suit.Pike, Rank.Four),
            Card(Suit.Pike, Rank.Five),
            Card(Suit.Pike, Rank.Six),
            Card(Suit.Pike, Rank.Seven),
            Card(Suit.Pike, Rank.Eight),
            Card(Suit.Pike, Rank.Nine),
            Card(Suit.Pike, Rank.Ten),
            Card(Suit.Pike, Rank.Jack)
        )
        val gameDecksHelper: GameDecksHelper = DeckHelper.generateFakeDealerEvents(
            AGGREGATE_ID,
            listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3, PLAYER_ID4),
            PLAYER_ID1,
            STRAIGHT_CARDS,
            DISCARD_DECK_CARD,
            STRAIGHT_CARDS2
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
            StraightDropped.create(
                aggregateId,
                PLAYER_ID1,
                STRAIGHT_ID, STRAIGHT_CARDS.plus(DISCARD_DECK_CARD)
            ),
            CardsPickedFromPlayerDeckDuringTurn.create(
                aggregateId,
                PLAYER_ID1,
                gameDecksHelper.getCardsFromPlayerDeck1()
            ),
            StraightDropped.create(aggregateId, PLAYER_ID1, STRAIGHT_ID, STRAIGHT_CARDS2)
        )
    )

    override fun `when`(): ICommand<Game> = PickUpPlayerDeckDuringTurn(aggregateId, PLAYER_ID1)

    override fun expected(): List<IDomainEvent> = listOf()

    override fun expectedException(): Exception? =
        IllegalArgumentException("Player ${PLAYER_ID1.valueAsString()}'s team has already pickedUp its playerDeck")
}

//
//internal class Given_GameExecutionPlayPhase_When_RightPlayerAddValidCardsToStraightNoBelongHim_Then_exception :
//    KcqrsAggregateTestSpecification<Game>() {
//    companion object {
//        val AGGREGATE_ID = GameIdentity.create()
//        val PLAYER_ID1 = PlayerIdentity.create()
//        val PLAYER_ID2 = PlayerIdentity.create()
//        val PLAYER_ID3 = PlayerIdentity.create()
//        val PLAYER_ID4 = PlayerIdentity.create()
//        val STRAIGHT_ID = StraightIdentity.create()
//        val STRAIGHT_CARDS = listOf(Card(Suit.Tile, Rank.Three), Card(Suit.Tile, Rank.Four), Card(Suit.Tile, Rank.Five))
//        val gameDecksHelper: GameDecksHelper = DeckHelper.generateFakeDealerEvents(AGGREGATE_ID, listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3, PLAYER_ID4), PLAYER_ID1, STRAIGHT_CARDS)
//    }
//
//    //Setup
//    override val aggregateId: GameIdentity = AGGREGATE_ID
//    override fun emptyAggregate(): (identity: IIdentity) -> GameDraft = { id -> GameDraft.empty() }
//    override fun streamNameRoot(): String = "Stream1"
//
//    //Test
//    override fun given(): List<IDomainEvent> = listOf<GameEvent>(
//        PlayerAdded.create(aggregateId, PLAYER_ID1),
//        PlayerAdded.create(aggregateId, PLAYER_ID2),
//        PlayerAdded.create(aggregateId, PLAYER_ID3),
//        PlayerAdded.create(aggregateId, PLAYER_ID4),
//        CardDealingRequested.create(aggregateId, PLAYER_ID1)
//    ).plus(
//        gameDecksHelper.events()
//    ).plus(
//        listOf(
//            GameStarted.create(aggregateId),
//            CardsPickedFromDiscardPile.create(aggregateId, PLAYER_ID1, gameDecksHelper.getCardsFromDiscardDeck()),
//            StraightDropped.create(aggregateId, PLAYER_ID1, Straight.create(STRAIGHT_ID, STRAIGHT_CARDS))
//        )
//    )
//
//    override fun `when`(): ICommand<Game> = AppendCardsOnStraight(aggregateId, PLAYER_ID1, STRAIGHT_ID, listOf(Card(Suit.Tile, Rank.Six)))
//
//    override fun expected(): List<IDomainEvent> = listOf()
//
//    override fun expectedException(): Exception? =
//        IllegalArgumentException("Cards to append to the straight ${STRAIGHT_ID.valueAsString()} don't belong to player ${PLAYER_ID1.valueAsString()}")
//}
//
//internal class Given_GameExecutionPlayPhase_When_WrongPlayerAddValidCardsToStraight_Then_exception :
//    KcqrsAggregateTestSpecification<Game>() {
//    companion object {
//        val AGGREGATE_ID = GameIdentity.create()
//        val PLAYER_ID1 = PlayerIdentity.create()
//        val PLAYER_ID2 = PlayerIdentity.create()
//        val PLAYER_ID3 = PlayerIdentity.create()
//        val PLAYER_ID4 = PlayerIdentity.create()
//        val STRAIGHT_ID = StraightIdentity.create()
//        val STRAIGHT_CARDS = listOf(Card(Suit.Heart, Rank.Three), Card(Suit.Heart, Rank.Four), Card(Suit.Heart, Rank.Five))
//        val gameDecksHelper: GameDecksHelper = DeckHelper.generateFakeDealerEvents(AGGREGATE_ID, listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3, PLAYER_ID4), PLAYER_ID2, STRAIGHT_CARDS)
//    }
//
//    //Setup
//    override val aggregateId: GameIdentity = AGGREGATE_ID
//    override fun emptyAggregate(): (identity: IIdentity) -> GameDraft = { id -> GameDraft.empty() }
//    override fun streamNameRoot(): String = "Stream1"
//
//    //Test
//    override fun given(): List<IDomainEvent> = listOf<GameEvent>(
//        PlayerAdded.create(aggregateId, PLAYER_ID1),
//        PlayerAdded.create(aggregateId, PLAYER_ID2),
//        PlayerAdded.create(aggregateId, PLAYER_ID3),
//        PlayerAdded.create(aggregateId, PLAYER_ID4),
//        CardDealingRequested.create(aggregateId, PLAYER_ID1)
//    ).plus(
//        gameDecksHelper.events()
//    ).plus(
//        listOf(
//            GameStarted.create(aggregateId),
//            CardsPickedFromDiscardPile.create(aggregateId, PLAYER_ID1, gameDecksHelper.getCardsFromDiscardDeck()),
//            StraightDropped.create(aggregateId, PLAYER_ID1, Straight.create(STRAIGHT_ID, STRAIGHT_CARDS))
//        )
//    )
//
//    override fun `when`(): ICommand<Game> = AppendCardsOnStraight(aggregateId, PLAYER_ID2, STRAIGHT_ID, listOf(Card(Suit.Heart, Rank.Six)))
//
//    override fun expected(): List<IDomainEvent> = listOf()
//
//    override fun expectedException(): Exception? = IllegalArgumentException("It's not the turn of the player ${PLAYER_ID2.valueAsString()}")
//}