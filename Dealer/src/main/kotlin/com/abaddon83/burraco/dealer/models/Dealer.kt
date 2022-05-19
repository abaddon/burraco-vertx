package com.abaddon83.burraco.dealer.models


import com.abaddon83.burraco.dealer.events.DeckCreated
import com.abaddon83.burraco.dealer.helpers.CardsHelper
import io.github.abaddon.kcqrs.core.domain.AggregateRoot
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

data class Dealer private constructor(
    override val id: DealerIdentity,
    override val version: Long,
    val gameIdentity: GameIdentity,
    val players: List<PlayerIdentity>,
    val cardsAvailable: List<Card>
) : AggregateRoot() {
    val log: Logger = LoggerFactory.getLogger(this::class.simpleName)
    override val uncommittedEvents: MutableCollection<IDomainEvent> = mutableListOf()

    companion object Factory {
        fun empty(): Dealer = Dealer(DealerIdentity.empty(), 0, GameIdentity.empty(), listOf(), listOf())
    }

    //2750
    //1500
    fun createDeck(dealerIdentity: DealerIdentity, gameIdentity: GameIdentity, players: List<PlayerIdentity>): Dealer {
        check(this.id == DealerIdentity.empty()) { "Current game with id ${this.id} is already created" }
        val cards = CardsHelper.allRanksWithJollyCards()
            .plus(CardsHelper.allRanksWithJollyCards())
            .shuffled()
        return raiseEvent(DeckCreated.create(dealerIdentity, gameIdentity, players, cards)) as Dealer
    }

    private fun apply(event: DeckCreated): Dealer {
        log.debug("The aggregate is applying the event ${event::class.simpleName} with id ${event.messageId}")
        val newDealer = copy(
            id = event.aggregateId,
            gameIdentity = event.gameId,
            players = event.playersId,
            cardsAvailable = event.cards
        )
        log.debug("New Player added, now there are ${newDealer.cardsAvailable.size} cards and ${newDealer.players.size} players")
        return newDealer
    }

//    fun addPlayer(playerIdentity: PlayerIdentity): Dealer {
//        require(!players.contains(playerIdentity)) { "The player ${playerIdentity.valueAsString()} is already a player of game ${this.id.valueAsString()}" }
//        val playersCount = players.size + 1
//        check(GameConfig.MAX_PLAYERS >= playersCount) { "Maximum number of players reached, (Max: ${GameConfig.MAX_PLAYERS})" }
//
//        return raiseEvent(PlayerAdded.create(id, playerIdentity)) as Dealer
//    }
//
//    fun removePlayer(playerIdentity: PlayerIdentity): Dealer {
//        require(players.contains(playerIdentity)) { "The player ${playerIdentity.valueAsString()} is not a player of game ${this.id.valueAsString()}" }
//
//        return raiseEvent(PlayerRemoved.create(id, playerIdentity)) as Dealer
//    }
//
//    fun requestDealCards(requestedBy: PlayerIdentity): GameWaitingDealer {
//        require(players.contains(requestedBy)) { "The player $requestedBy is not one of the players " }
//        check(players.size in GameConfig.MIN_PLAYERS..GameConfig.MAX_PLAYERS) { "Not enough players to deal the playing cards, ( Min players required: ${GameConfig.MIN_PLAYERS})" }
//
//        return raiseEvent(CardDealingRequested.create(id, requestedBy)) as GameWaitingDealer
//    }

//    private fun apply(event: GameCreated): Dealer {
//        log.debug("The aggregate is applying the event ${event::class.simpleName} with id ${event.messageId}")
//        return copy(id = event.aggregateId)
//    }
//
//    private fun apply(event: PlayerAdded): Dealer {
//        log.debug("The aggregate is applying the event ${event::class.simpleName} with id ${event.messageId}")
//        val updatedPlayers = players.plus(WaitingPlayer(event.playerIdentity))
//        log.debug("New Player added, now there are ${updatedPlayers.size} players")
//        return copy(players = updatedPlayers)
//    }
//
//    private fun apply(event: PlayerRemoved): Dealer {
//        log.debug("The aggregate is applying the event ${event::class.simpleName} with id ${event.messageId}")
//        val updatedPlayers = players.minus(WaitingPlayer(event.playerIdentity))
//        log.debug("Player removed, now there are ${updatedPlayers.size} players")
//        return copy(players = updatedPlayers)
//    }
//
//    private fun apply(event: CardDealingRequested): GameWaitingDealer {
//        log.debug("The aggregate is applying the event ${event::class.simpleName} with id ${event.messageId}")
//        return GameWaitingDealer.from(this)
//    }

}
