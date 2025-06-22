package com.abaddon83.burraco.game.commands.gameExecutionEndPhase

import com.abaddon83.burraco.game.events.game.*
import com.abaddon83.burraco.game.models.Straight
import com.abaddon83.burraco.common.models.StraightIdentity
import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameDraft
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.card.Rank
import com.abaddon83.burraco.common.models.card.Suit
import com.abaddon83.burraco.helper.DeckHelper
import com.abaddon83.burraco.helper.GameDecksHelper
import io.github.abaddon.kcqrs.core.IIdentity
import io.github.abaddon.kcqrs.core.domain.messages.commands.ICommand
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.github.abaddon.kcqrs.test.KcqrsAggregateTestSpecification

internal class Given_GameExecutionEndPhase_and_GameNotEnded_When_StartNextPlayerTurn_Then_event :
    KcqrsAggregateTestSpecification<Game>() {
    companion object {
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID1 = PlayerIdentity.create()
        val PLAYER_ID2 = PlayerIdentity.create()
        val PLAYER_ID3 = PlayerIdentity.create()
        val PLAYER_ID4 = PlayerIdentity.create()
        val STRAIGHT_ID = StraightIdentity.create()
        val STRAIGHT_CARDS = listOf(Card(Suit.Heart, Rank.Three), Card(Suit.Heart, Rank.Four), Card(Suit.Heart, Rank.Five))
        val CARD_TO_DROP = Card(Suit.Heart, Rank.Six)
        val gameDecksHelper: GameDecksHelper = DeckHelper.generateFakeDealerEvents(
            AGGREGATE_ID,
            listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3, PLAYER_ID4),
            PLAYER_ID1,
            STRAIGHT_CARDS.plus(CARD_TO_DROP)
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
            StraightDropped.create(aggregateId, PLAYER_ID1, Straight.create(STRAIGHT_ID, STRAIGHT_CARDS)),
            CardsDiscarded.create(aggregateId, PLAYER_ID1,CARD_TO_DROP)
        )
    )

    override fun `when`(): ICommand<Game> = StartNextPlayerTurn(aggregateId)

    override fun expected(): List<IDomainEvent> = listOf(
        NextPlayerTurnStarted.create(aggregateId, PLAYER_ID2)
    )

    override fun expectedException(): Exception? = null
}

internal class Given_GameExecutionEndPhase_and_GamEnded_When_StartNextPlayerTurn_Then_exception :
    KcqrsAggregateTestSpecification<Game>() {
    companion object {
        val AGGREGATE_ID = GameIdentity.create()
        val PLAYER_ID1 = PlayerIdentity.create()
        val PLAYER_ID2 = PlayerIdentity.create()
        val PLAYER_ID3 = PlayerIdentity.create()
        val PLAYER_ID4 = PlayerIdentity.create()
        val STRAIGHT_ID1 = StraightIdentity.create()
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
        val STRAIGHT_ID2 = StraightIdentity.create()
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
            Card(Suit.Pike, Rank.Ten)
        )
        val CARD_TO_DROP = Card(Suit.Clover, Rank.Six)
        val PLAYER_DECK_CARD = STRAIGHT_CARDS2.plus(CARD_TO_DROP)
        val DISCARD_DECK_CARD = Card(Suit.Heart, Rank.Queen)
        val gameDecksHelper: GameDecksHelper = DeckHelper.generateFakeDealerEvents(
            gameId = AGGREGATE_ID,
            players = listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3, PLAYER_ID4),
            playerIdentitySelected = PLAYER_ID1,
            cardsSelected= STRAIGHT_CARDS,
            discardDeckCard = DISCARD_DECK_CARD,
            playerDeck1Selected = PLAYER_DECK_CARD
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
            StraightDropped.create(aggregateId, PLAYER_ID1, Straight.create(STRAIGHT_ID1, STRAIGHT_CARDS.plus(DISCARD_DECK_CARD))),
            CardsPickedFromPlayerDeckDuringTurn.create(aggregateId, PLAYER_ID1, gameDecksHelper.getCardsFromPlayerDeck1()),
            StraightDropped.create(aggregateId, PLAYER_ID1, Straight.create(STRAIGHT_ID2, STRAIGHT_CARDS2)),
            CardsDiscarded.create(aggregateId, PLAYER_ID1,CARD_TO_DROP)
        )
    )

    override fun `when`(): ICommand<Game> = StartNextPlayerTurn(aggregateId)

    override fun expected(): List<IDomainEvent> = listOf()

    override fun expectedException(): Exception? = IllegalStateException("The game is ended, the nex player can't start his/her turn")
}