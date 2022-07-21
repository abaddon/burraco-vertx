package com.abaddon83.burraco.dealer.services

import com.abaddon83.burraco.dealer.commands.*
import com.abaddon83.burraco.dealer.models.Dealer
import com.abaddon83.burraco.dealer.models.DealerConfig.MAX_DISCARD_DECK_CARD
import com.abaddon83.burraco.dealer.models.DealerConfig.MAX_PLAYER_CARD
import com.abaddon83.burraco.dealer.models.DealerConfig.MAX_PLAYER_DECK_CARD
import com.abaddon83.burraco.dealer.models.DealerConfig.SINGLE_DECK_CARD_WITH_JOLLY
import com.abaddon83.burraco.dealer.models.DealerIdentity
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.dealer.ports.DomainError
import com.abaddon83.burraco.dealer.ports.DomainResult
import com.abaddon83.burraco.dealer.ports.ExceptionError
import io.github.abaddon.kcqrs.core.domain.IAggregateCommandHandler
import io.github.abaddon.kcqrs.core.domain.Result


sealed class DealerServiceResult<out TDomainError: DomainError, out DomainResult> {
    data class Invalid<TDomainError: DomainError>(val err: TDomainError): DealerServiceResult<TDomainError, Nothing>()
    data class Valid<DomainResult>(val value: DomainResult): DealerServiceResult<Nothing, DomainResult>()
}

class DealerService(private val dealerCommandHandler: IAggregateCommandHandler<Dealer>) {

    //typealias Outcome = Validated<DomainError, DomainResult>

    suspend fun dealCards(
        dealerIdentity: DealerIdentity,
        gameIdentity: GameIdentity,
        players: List<PlayerIdentity>
    ): DealerServiceResult<DomainError,DomainResult> = try {
        //create deck
        when (val createDecksResult = dealerCommandHandler.handle(CreateDecks(dealerIdentity, gameIdentity, players))) {
            is Result.Invalid -> DealerServiceResult.Invalid(ExceptionError(createDecksResult.err))
            //deal players cards
            is Result.Valid -> when (val dealPlayersCardsResult =
                dealPlayersCards(dealerIdentity, gameIdentity, players, MAX_PLAYER_CARD, players)) {
                is Result.Invalid -> DealerServiceResult.Invalid(ExceptionError(dealPlayersCardsResult.err))
                //deal playerDeck1
                is Result.Valid -> when (val dealPlayerDeck1Result =
                    dealPlayerDeck1(dealerIdentity, gameIdentity, MAX_PLAYER_DECK_CARD[players.size % 2])) {
                    is Result.Invalid -> DealerServiceResult.Invalid(ExceptionError(dealPlayerDeck1Result.err))
                    //deal playerDeck2
                    is Result.Valid -> when (val dealPlayerDeck2Result =
                        dealPlayerDeck2(dealerIdentity, gameIdentity, MAX_PLAYER_DECK_CARD[0])) {
                        is Result.Invalid -> DealerServiceResult.Invalid(ExceptionError(dealPlayerDeck2Result.err))
                        //deal discard Deck
                        is Result.Valid -> when (val dealDiscardDeckResult =
                            dealDiscardDeck(dealerIdentity, gameIdentity)) {
                            is Result.Invalid -> DealerServiceResult.Invalid(ExceptionError(dealDiscardDeckResult.err))
                            //deal Deck
                            is Result.Valid -> when (val dealDeckResult = dealDeck(dealerIdentity, gameIdentity)) {
                                is Result.Invalid -> DealerServiceResult.Invalid(ExceptionError(dealDeckResult.err))
                                is Result.Valid -> {
                                    val dealer = dealDeckResult.value
                                    dealer.players.forEach { player -> check(player.numCardsDealt == MAX_PLAYER_CARD) }
                                    check(dealer.cardsAvailable.isEmpty())
                                    check(dealer.playerDeck1NumCards == MAX_PLAYER_DECK_CARD[players.size % 2])
                                    check(dealer.playerDeck2NumCards == MAX_PLAYER_DECK_CARD[0])
                                    check(dealer.discardDeck == MAX_DISCARD_DECK_CARD)
                                    val totalCards = (SINGLE_DECK_CARD_WITH_JOLLY * 2)
                                    val expectedDeckCards =
                                        totalCards - dealer.players.sumOf { it.numCardsDealt } - dealer.playerDeck1NumCards - dealer.playerDeck2NumCards - dealer.discardDeck
                                    check(dealer.deckNumCards == expectedDeckCards)
                                    DealerServiceResult.Valid(DomainResult(dealer.uncommittedEvents, dealer))
                                }
                            }
                        }
                    }
                }
            }
        }
    } catch (e: Exception) {
        DealerServiceResult.Invalid(ExceptionError(e))
    }


    private suspend fun dealDeck(
        dealerIdentity: DealerIdentity,
        gameIdentity: GameIdentity
    ): Result<Exception, Dealer> =
        when (val result = dealerCommandHandler.handle(DealCardToDeck(dealerIdentity, gameIdentity))) {
            is Result.Invalid -> result
            is Result.Valid -> {
                when (result.value.cardsAvailable.isNotEmpty()) {
                    true -> dealDeck(dealerIdentity, gameIdentity)
                    false -> result
                }
            }
        }

    private suspend fun dealDiscardDeck(
        dealerIdentity: DealerIdentity,
        gameIdentity: GameIdentity
    ): Result<Exception, Dealer> =
        dealerCommandHandler.handle(
            DealCardToDiscardDeck(dealerIdentity, gameIdentity)
        )

    private suspend fun dealPlayerDeck1(
        dealerIdentity: DealerIdentity,
        gameIdentity: GameIdentity,
        remainingCards: Int,
    ): Result<Exception, Dealer> =
        when (val result = dealerCommandHandler.handle(DealCardToPlayerDeck1(dealerIdentity, gameIdentity))) {
            is Result.Invalid -> result
            is Result.Valid -> {
                val updatedRemainingCards = remainingCards - 1
                when (updatedRemainingCards > 0) {
                    true -> dealPlayerDeck1(dealerIdentity, gameIdentity, updatedRemainingCards)
                    false -> result
                }
            }
        }


    private suspend fun dealPlayerDeck2(
        dealerIdentity: DealerIdentity,
        gameIdentity: GameIdentity,
        remainingCards: Int,
    ): Result<Exception, Dealer> =
        when (val result = dealerCommandHandler.handle(DealCardToPlayerDeck2(dealerIdentity, gameIdentity))) {
            is Result.Invalid -> result
            is Result.Valid -> {
                val updatedRemainingCards = remainingCards - 1
                when (updatedRemainingCards > 0) {
                    true -> dealPlayerDeck2(dealerIdentity, gameIdentity, updatedRemainingCards)
                    false -> result
                }
            }
        }


    private suspend fun dealPlayersCards(
        dealerIdentity: DealerIdentity,
        gameIdentity: GameIdentity,
        playerIdentities: List<PlayerIdentity>,
        remainingCards: Int,
        playerRemaining: List<PlayerIdentity>
    ): Result<Exception, Dealer> {

        val currentPlayer = playerRemaining.first()
        return when (val result = dealPlayersCard(dealerIdentity, gameIdentity, currentPlayer)) {
            is Result.Invalid -> result
            is Result.Valid -> {
                val currentPlayerRemaining = playerRemaining.minus(currentPlayer)
                val (updatedCardRemaining, updatedPlayerRemaining) = when (currentPlayerRemaining.isEmpty()) {
                    true -> Pair(remainingCards - 1, playerIdentities)
                    false -> Pair(remainingCards, currentPlayerRemaining)
                }
                when (updatedCardRemaining > 0) {
                    true -> dealPlayersCards(
                        dealerIdentity,
                        gameIdentity,
                        playerIdentities,
                        updatedCardRemaining,
                        updatedPlayerRemaining
                    )
                    false -> result
                }
            }
        }
    }


    private suspend fun dealPlayersCard(
        dealerIdentity: DealerIdentity,
        gameIdentity: GameIdentity,
        playerIdentity: PlayerIdentity
    ): Result<Exception, Dealer> =
        dealerCommandHandler.handle(
            DealCardToPlayer(dealerIdentity, gameIdentity, playerIdentity)
        )
}