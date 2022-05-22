package com.abaddon83.burraco.dealer.commands

import com.abaddon83.burraco.dealer.events.CardDealtToDeck
import com.abaddon83.burraco.dealer.events.DealerEvent
import com.abaddon83.burraco.dealer.events.DeckCreated
import com.abaddon83.burraco.dealer.helpers.CardsHelper
import com.abaddon83.burraco.dealer.models.Dealer
import com.abaddon83.burraco.dealer.models.DealerIdentity
import com.abaddon83.burraco.dealer.models.GameIdentity
import com.abaddon83.burraco.dealer.models.PlayerIdentity
import io.github.abaddon.kcqrs.core.IIdentity
import io.github.abaddon.kcqrs.core.domain.messages.commands.ICommand
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.github.abaddon.kcqrs.test.KcqrsAggregateTestSpecification
import org.junit.jupiter.api.Assertions.*

internal class Given_existingDealer_When_DealCardToDeck_Then_event : KcqrsAggregateTestSpecification<Dealer>(){
    companion object{
        val AGGREGATE_ID = DealerIdentity.create()
        val PLAYER_ID1= PlayerIdentity.create()
        val PLAYER_ID2= PlayerIdentity.create()
        val PLAYER_ID3= PlayerIdentity.create()
        val PLAYERS = listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3)
        val GAME_ID = GameIdentity.create()
        val CARDS = CardsHelper.allRanksWithJollyCards()
            .plus(CardsHelper.allRanksWithJollyCards())
            .shuffled()

    }
    //Setup
    override val aggregateId: DealerIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> Dealer ={ Dealer.empty() }
    override fun streamNameRoot(): String ="Stream1"

    //Test
    override fun given(): List<IDomainEvent> = listOf<DealerEvent>(
        DeckCreated.create(AGGREGATE_ID, GAME_ID, PLAYERS, CARDS)
    )

    override fun `when`(): ICommand<Dealer> = DealCardToDeck(aggregateId, gameIdentity = GAME_ID)

    override fun expected(): List<IDomainEvent> = listOf(
        CardDealtToDeck.create(aggregateId, GAME_ID, CARDS.first())
    )

    override fun expectedException(): Exception? = null
}

internal class Given_nothing_When_DealCardToDeck_Then_exception : KcqrsAggregateTestSpecification<Dealer>(){
    companion object{
        val AGGREGATE_ID = DealerIdentity.create()
        val PLAYER_ID1= PlayerIdentity.create()
        val PLAYER_ID2= PlayerIdentity.create()
        val PLAYER_ID3= PlayerIdentity.create()
        val PLAYERS = listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3)
        val GAME_ID = GameIdentity.create()
        val CARDS = CardsHelper.allRanksWithJollyCards()
            .plus(CardsHelper.allRanksWithJollyCards())
            .shuffled()

    }
    //Setup
    override val aggregateId: DealerIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> Dealer ={ Dealer.empty() }
    override fun streamNameRoot(): String ="Stream1"

    //Test
    override fun given(): List<IDomainEvent> = listOf<DealerEvent>(

    )

    override fun `when`(): ICommand<Dealer> = DealCardToDeck(aggregateId, gameIdentity = GAME_ID)

    override fun expected(): List<IDomainEvent> = listOf(
    )

    override fun expectedException(): Exception? = IllegalStateException("Current dealer with id ${DealerIdentity.empty().valueAsString()} is not yet created")
}

internal class Given_existingDealer_When_DealCardToDeckWithWrongGamId_Then_exception : KcqrsAggregateTestSpecification<Dealer>(){
    companion object{
        val AGGREGATE_ID = DealerIdentity.create()
        val PLAYER_ID1= PlayerIdentity.create()
        val PLAYER_ID2= PlayerIdentity.create()
        val PLAYER_ID3= PlayerIdentity.create()
        val PLAYERS = listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3)
        val GAME_ID = GameIdentity.create()
        val CARDS = CardsHelper.allRanksWithJollyCards()
            .plus(CardsHelper.allRanksWithJollyCards())
            .shuffled()
        val WRONG_GAME_ID = GameIdentity.create()

    }
    //Setup
    override val aggregateId: DealerIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> Dealer ={ Dealer.empty() }
    override fun streamNameRoot(): String ="Stream1"

    //Test
    override fun given(): List<IDomainEvent> = listOf<DealerEvent>(
        DeckCreated.create(AGGREGATE_ID, GAME_ID, PLAYERS, CARDS)
    )

    override fun `when`(): ICommand<Dealer> = DealCardToDeck(aggregateId, gameIdentity = WRONG_GAME_ID)

    override fun expected(): List<IDomainEvent> = listOf(

    )

    override fun expectedException(): Exception? = IllegalArgumentException("Game ${WRONG_GAME_ID.valueAsString()} is different")
}

internal class Given_existingDealerWithDealCardToDeck_When_DealCardToDeck_Then_event : KcqrsAggregateTestSpecification<Dealer>(){
    companion object{
        val AGGREGATE_ID = DealerIdentity.create()
        val PLAYER_ID1= PlayerIdentity.create()
        val PLAYER_ID2= PlayerIdentity.create()
        val PLAYER_ID3= PlayerIdentity.create()
        val PLAYERS = listOf(PLAYER_ID1, PLAYER_ID2, PLAYER_ID3)
        val GAME_ID = GameIdentity.create()
        val CARDS = CardsHelper.allRanksWithJollyCards()
            .plus(CardsHelper.allRanksWithJollyCards())
            .shuffled()

    }
    //Setup
    override val aggregateId: DealerIdentity = AGGREGATE_ID
    override fun emptyAggregate(): (identity: IIdentity) -> Dealer ={ Dealer.empty() }
    override fun streamNameRoot(): String ="Stream1"

    //Test
    override fun given(): List<IDomainEvent> = listOf<DealerEvent>(
        DeckCreated.create(AGGREGATE_ID, GAME_ID, PLAYERS, CARDS),
        CardDealtToDeck.create(aggregateId, GAME_ID, CARDS.elementAt(0))
    )

    override fun `when`(): ICommand<Dealer> = DealCardToDeck(aggregateId, gameIdentity = GAME_ID)

    override fun expected(): List<IDomainEvent> = listOf(
        CardDealtToDeck.create(aggregateId, GAME_ID, CARDS.elementAt(1))
    )

    override fun expectedException(): Exception? = null
}