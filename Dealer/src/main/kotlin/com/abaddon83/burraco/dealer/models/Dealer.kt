package com.abaddon83.burraco.dealer.models


import com.abaddon83.burraco.common.models.DealerIdentity
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.card.Card
import com.abaddon83.burraco.common.models.event.dealer.CardDealtToDeck
import com.abaddon83.burraco.common.models.event.dealer.CardDealtToDiscardDeck
import com.abaddon83.burraco.common.models.event.dealer.CardDealtToPlayer
import com.abaddon83.burraco.common.models.event.dealer.CardDealtToPlayerDeck1
import com.abaddon83.burraco.common.models.event.dealer.CardDealtToPlayerDeck2
import com.abaddon83.burraco.common.models.event.dealer.DealingCompleted
import com.abaddon83.burraco.common.models.event.dealer.DeckCreated
import com.abaddon83.burraco.dealer.helpers.CardsHelper
import com.abaddon83.burraco.dealer.helpers.contains
import com.abaddon83.burraco.dealer.helpers.playerNumCards
import com.abaddon83.burraco.dealer.helpers.removeCards
import com.abaddon83.burraco.dealer.helpers.updatePlayer
import com.abaddon83.burraco.dealer.models.DealerConfig.MAX_DISCARD_DECK_CARD
import com.abaddon83.burraco.dealer.models.DealerConfig.MAX_PLAYER_CARD
import com.abaddon83.burraco.dealer.models.DealerConfig.MAX_PLAYER_DECK_CARD
import io.github.abaddon.kcqrs.core.domain.AggregateRoot
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.github.abaddon.kcqrs.core.helpers.KcqrsLoggerFactory.log

data class Dealer(
    override val id: DealerIdentity,
    override val version: Long,
    val gameIdentity: GameIdentity,
    val players: List<Player>,
    val playerDeck1NumCards: Int,
    val playerDeck2NumCards: Int,
    val discardDeck: Int,
    val deckNumCards: Int,
    val cardsAvailable: List<Card>
) : AggregateRoot() {
    override val uncommittedEvents: MutableCollection<IDomainEvent> = mutableListOf()


    companion object Factory {
        fun empty(): Dealer = Dealer(DealerIdentity.empty(), 0, GameIdentity.empty(), listOf(), 0, 0, 0, 0, listOf())
//        fun empty(identity: DealerIdentity): Dealer =
//            Dealer(identity, 0, GameIdentity.empty(), listOf(), 0, 0, 0, 0, listOf())
    }

    fun createDeck(dealerIdentity: DealerIdentity, gameIdentity: GameIdentity, players: List<PlayerIdentity>): Dealer {
        check(this.id == DealerIdentity.empty()) { "Current dealer with id ${this.id.valueAsString()} is already created" }
        check(players.size in 2..4) { "The number of Players is wrong, current number of player: ${players.size}" }
        val cards = CardsHelper.allRanksWithJollyCards()
            .plus(CardsHelper.allRanksWithJollyCards())
            .shuffled()
        return raiseEvent(DeckCreated.create(dealerIdentity, gameIdentity, players, cards)) as Dealer
    }

    fun dealCardToPlayer(gameId: GameIdentity, playerId: PlayerIdentity): Dealer {
        check(this.id != DealerIdentity.empty()) { "Current dealer with id ${this.id.valueAsString()} is not yet created" }
        check(players.playerNumCards(playerId) < MAX_PLAYER_CARD) {
            "The player ${playerId.valueAsString()} has the maximum number of cards (${
                players.playerNumCards(
                    playerId
                )
            })"
        }
        require(this.gameIdentity == gameId) { "Game ${gameId.valueAsString()} is different" }
        require(this.players.contains(playerId)) { "Player ${playerId.valueAsString()} is not part of the game" }

        return raiseEvent(CardDealtToPlayer.create(id, gameId, playerId, cardsAvailable.first())) as Dealer
    }

    fun dealCardToPlayerDeck1(gameId: GameIdentity): Dealer {
        check(this.id != DealerIdentity.empty()) { "Current dealer with id ${this.id.valueAsString()} is not yet created" }
        check(playerDeck1NumCards < MAX_PLAYER_DECK_CARD[players.size % 2]) { "The playerDeck1NumCards has the maximum number of cards (${playerDeck1NumCards})" }
        require(this.gameIdentity == gameId) { "Game ${gameId.valueAsString()} is different" }

        return raiseEvent(CardDealtToPlayerDeck1.create(id, gameId, cardsAvailable.first())) as Dealer
    }

    fun dealCardToPlayerDeck2(gameId: GameIdentity): Dealer {
        check(this.id != DealerIdentity.empty()) { "Current dealer with id ${this.id.valueAsString()} is not yet created" }
        check(playerDeck2NumCards < MAX_PLAYER_DECK_CARD[0]) { "The playerDeck2NumCards has the maximum number of cards (${playerDeck2NumCards})" }
        require(this.gameIdentity == gameId) { "Game ${gameId.valueAsString()} is different" }

        return raiseEvent(CardDealtToPlayerDeck2.create(id, gameId, cardsAvailable.first())) as Dealer
    }

    fun dealCardToDiscardDeck(gameId: GameIdentity): Dealer {
        check(this.id != DealerIdentity.empty()) { "Current dealer with id ${this.id.valueAsString()} is not yet created" }
        check(discardDeck < MAX_DISCARD_DECK_CARD) { "The discard deck has the maximum number of cards (${discardDeck})" }
        require(this.gameIdentity == gameId) { "Game ${gameId.valueAsString()} is different" }

        return raiseEvent(CardDealtToDiscardDeck.create(id, gameId, cardsAvailable.first())) as Dealer
    }

    fun dealCardToDeck(gameId: GameIdentity): Dealer {
        check(this.id != DealerIdentity.empty()) { "Current dealer with id ${this.id.valueAsString()} is not yet created" }
        require(this.gameIdentity == gameId) { "Game ${gameId.valueAsString()} is different" }

        return raiseEvent(CardDealtToDeck.create(id, gameId, cardsAvailable.first())) as Dealer
    }

    fun completeDealingCards(gameId: GameIdentity): Dealer {
        check(this.id != DealerIdentity.empty()) { "Current dealer with id ${this.id.valueAsString()} is not yet created" }
        require(this.gameIdentity == gameId) { "Game ${gameId.valueAsString()} is different" }
        check(cardsAvailable.isEmpty()) { "Cards still available, dealing not completed" }

        return raiseEvent(DealingCompleted.create(id, gameId)) as Dealer
    }


    private fun apply(event: DeckCreated): Dealer {
        log.debug("The aggregate is applying the event ${event::class.simpleName} with id ${event.messageId}")
        val newDealer = copy(
            version = 0,
            id = event.aggregateId,
            gameIdentity = event.gameId,
            players = event.playersId.map { Player.create(it) },
            cardsAvailable = event.cards
        )
        log.debug("Dealer initialised, now there are ${newDealer.cardsAvailable.size} cards and ${newDealer.players.size} players")
        return newDealer
    }

    private fun apply(event: CardDealtToPlayer): Dealer {
        log.debug("The aggregate is applying the event ${event::class.simpleName} with id ${event.messageId}")
        val newDealer = copy(
            version = this.version + 1,
            cardsAvailable = cardsAvailable.removeCards(listOf(event.card)),
            players = players.updatePlayer(event.playerId) { player -> player.copy(numCardsDealt = player.numCardsDealt + 1) }
        )
        log.debug(
            "Card dealt to Player, now there are ${newDealer.cardsAvailable.size} cards and player ${event.playerId.valueAsString()} has ${
                newDealer.players.playerNumCards(
                    event.playerId
                )
            }"
        )
        return newDealer
    }

    private fun apply(event: CardDealtToPlayerDeck1): Dealer {
        log.debug("The aggregate is applying the event ${event::class.simpleName} with id ${event.messageId}")
        val newDealer = copy(
            version = this.version + 1,
            cardsAvailable = cardsAvailable.removeCards(listOf(event.card)),
            playerDeck1NumCards = this.playerDeck1NumCards + 1
        )
        log.debug("Card dealt to PlayerDeck1, now there are ${newDealer.cardsAvailable.size} cards and PlayerDeck1 has ${newDealer.playerDeck1NumCards} cards")
        return newDealer
    }

    private fun apply(event: CardDealtToPlayerDeck2): Dealer {
        log.debug("The aggregate is applying the event ${event::class.simpleName} with id ${event.messageId}")
        val newDealer = copy(
            version = this.version + 1,
            cardsAvailable = cardsAvailable.removeCards(listOf(event.card)),
            playerDeck2NumCards = this.playerDeck2NumCards + 1
        )
        log.debug("Card dealt to PlayerDeck1, now there are ${newDealer.cardsAvailable.size} cards and PlayerDeck1 has ${newDealer.playerDeck2NumCards} cards")
        return newDealer
    }

    private fun apply(event: CardDealtToDiscardDeck): Dealer {
        log.debug("The aggregate is applying the event ${event::class.simpleName} with id ${event.messageId}")
        val newDealer = copy(
            version = this.version + 1,
            cardsAvailable = cardsAvailable.removeCards(listOf(event.card)),
            discardDeck = this.discardDeck + 1
        )
        log.debug("Card dealt to Discard deck, now there are ${newDealer.cardsAvailable.size} cards and Discard deck has ${newDealer.discardDeck} cards")
        return newDealer
    }

    private fun apply(event: CardDealtToDeck): Dealer {
        log.debug("The aggregate is applying the event ${event::class.simpleName} with id ${event.messageId}")
        val newDealer = copy(
            version = this.version + 1,
            cardsAvailable = cardsAvailable.removeCards(listOf(event.card)),
            deckNumCards = this.deckNumCards + 1
        )
        log.debug("Card dealt to Discard deck, now there are ${newDealer.cardsAvailable.size} cards and Deck has ${newDealer.deckNumCards} cards")
        return newDealer
    }

    private fun apply(event: DealingCompleted): Dealer {
        log.debug("The aggregate is applying the event ${event::class.simpleName} with id ${event.messageId}")
        log.info("Dealing completed for game ${event.gameId.valueAsString()}")
        return copy(version = this.version + 1)
    }

}
