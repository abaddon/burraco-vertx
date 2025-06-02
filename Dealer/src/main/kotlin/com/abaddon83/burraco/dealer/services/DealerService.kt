package com.abaddon83.burraco.dealer.services

import com.abaddon83.burraco.common.helpers.Validated
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.dealer.DomainError
import com.abaddon83.burraco.dealer.DomainResult
import com.abaddon83.burraco.dealer.ExceptionError
import com.abaddon83.burraco.dealer.models.DealerConfig.MAX_DISCARD_DECK_CARD
import com.abaddon83.burraco.dealer.models.DealerConfig.MAX_PLAYER_CARD
import com.abaddon83.burraco.dealer.models.DealerConfig.MAX_PLAYER_DECK_CARD
import com.abaddon83.burraco.dealer.models.DealerConfig.SINGLE_DECK_CARD_WITH_JOLLY
import com.abaddon83.burraco.common.models.DealerIdentity
import com.abaddon83.burraco.dealer.ports.CommandControllerPort
import com.abaddon83.burraco.dealer.ports.Outcome
import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log


sealed class DealerServiceResult<out TDomainError: DomainError, out DomainResult> {
    data class Invalid<TDomainError: DomainError>(val err: TDomainError): DealerServiceResult<TDomainError, Nothing>()
    data class Valid<DomainResult>(val value: DomainResult): DealerServiceResult<Nothing, DomainResult>()
}

class DealerService(private val commandController: CommandControllerPort) {
    suspend fun dealCards(
        dealerIdentity: DealerIdentity,
        gameIdentity: GameIdentity,
        players: List<PlayerIdentity>
    ): DealerServiceResult<DomainError, DomainResult> = try {
        log.debug("service.dealCards: dealerIdentity: ${dealerIdentity.identity}, gameIdentity: $gameIdentity")
        //create deck
        when (val createDecksResult = commandController.createDecks(dealerIdentity, gameIdentity, players)) {
            is Validated.Invalid -> DealerServiceResult.Invalid(createDecksResult.err)
            //deal players cards
            is Validated.Valid -> when (val dealPlayersCardsResult =
                dealPlayersCards(createDecksResult.value.dealer.id, gameIdentity, players, MAX_PLAYER_CARD, players)) {
                is Validated.Invalid -> DealerServiceResult.Invalid(dealPlayersCardsResult.err)
                //deal playerDeck1
                is Validated.Valid -> when (val dealPlayerDeck1Result =
                    dealPlayerDeck1(dealerIdentity, gameIdentity, MAX_PLAYER_DECK_CARD[players.size % 2])) {
                    is Validated.Invalid -> DealerServiceResult.Invalid(dealPlayerDeck1Result.err)
                    //deal playerDeck2
                    is Validated.Valid -> when (val dealPlayerDeck2Result =
                        dealPlayerDeck2(dealerIdentity, gameIdentity, MAX_PLAYER_DECK_CARD[0])) {
                        is Validated.Invalid -> DealerServiceResult.Invalid(dealPlayerDeck2Result.err)
                        //deal discard Deck
                        is Validated.Valid -> when (val dealDiscardDeckResult =
                            dealDiscardDeck(dealerIdentity, gameIdentity)) {
                            is Validated.Invalid -> DealerServiceResult.Invalid(dealDiscardDeckResult.err)
                            //deal Deck
                            is Validated.Valid -> when (val dealDeckResult = dealDeck(dealerIdentity, gameIdentity)) {
                                is Validated.Invalid -> DealerServiceResult.Invalid(dealDeckResult.err)
                                is Validated.Valid -> {
                                    val dealer = dealDeckResult.value.dealer
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
    ): Outcome {
        log.debug("service.dealDeck: dealerIdentity: $dealerIdentity, gameIdentity: $gameIdentity")
        return when (val result = commandController.dealCardToDeck(dealerIdentity, gameIdentity)) {
            is Validated.Invalid -> result
            is Validated.Valid -> {
                when (result.value.dealer.cardsAvailable.isNotEmpty()) {
                    true -> dealDeck(dealerIdentity, gameIdentity)
                    false -> Validated.Valid(result.value)
                }
            }
        }
    }

    private suspend fun dealDiscardDeck(
        dealerIdentity: DealerIdentity,
        gameIdentity: GameIdentity
    ): Outcome = commandController.dealCardToDiscardDeck(dealerIdentity, gameIdentity)

    private suspend fun dealPlayerDeck1(
        dealerIdentity: DealerIdentity,
        gameIdentity: GameIdentity,
        remainingCards: Int,
    ): Outcome {
        log.debug("service.dealPlayerDeck1: dealerIdentity: $dealerIdentity, gameIdentity: $gameIdentity, remainingCards: $remainingCards")
        return when (val result = commandController.dealCardToPlayerDeck1(dealerIdentity, gameIdentity)) {
            is Validated.Invalid -> result
            is Validated.Valid -> {
                val updatedRemainingCards = remainingCards - 1
                when (updatedRemainingCards > 0) {
                    true -> dealPlayerDeck1(dealerIdentity, gameIdentity, updatedRemainingCards)
                    false -> result
                }
            }
        }
    }

    private suspend fun dealPlayerDeck2(
        dealerIdentity: DealerIdentity,
        gameIdentity: GameIdentity,
        remainingCards: Int,
    ): Outcome {
        log.debug("service.dealPlayerDeck2: dealerIdentity: $dealerIdentity, gameIdentity: $gameIdentity, remainingCards: $remainingCards")
        return when (val result = commandController.dealCardToPlayerDeck2(dealerIdentity, gameIdentity)) {
            is Validated.Invalid -> result
            is Validated.Valid -> {
                val updatedRemainingCards = remainingCards - 1
                when (updatedRemainingCards > 0) {
                    true -> dealPlayerDeck2(dealerIdentity, gameIdentity, updatedRemainingCards)
                    false -> result
                }
            }
        }
    }



    private suspend fun dealPlayersCards(
        dealerIdentity: DealerIdentity,
        gameIdentity: GameIdentity,
        playerIdentities: List<PlayerIdentity>,
        remainingCards: Int,
        playerRemaining: List<PlayerIdentity>
    ): Outcome {
        log.debug("service.dealPlayersCards: dealerIdentity: $dealerIdentity, gameIdentity: $gameIdentity, remainingCards: $remainingCards, playerRemainingCount: ${playerRemaining.count()}")
        val currentPlayer = playerRemaining.first()
        return when (val result = dealPlayersCard(dealerIdentity, gameIdentity, currentPlayer)) {
            is Validated.Invalid -> result
            is Validated.Valid -> {
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
    ): Outcome = commandController.dealCardToPlayer(dealerIdentity, gameIdentity, playerIdentity)

}