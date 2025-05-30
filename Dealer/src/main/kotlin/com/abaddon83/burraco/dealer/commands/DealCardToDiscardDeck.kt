package com.abaddon83.burraco.dealer.commands

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.dealer.models.Dealer
import com.abaddon83.burraco.dealer.models.DealerIdentity
import io.github.abaddon.kcqrs.core.domain.messages.commands.Command

data class DealCardToDiscardDeck(
    override val aggregateID: DealerIdentity,
    val gameIdentity: GameIdentity
) : Command<Dealer>(aggregateID) {

    override fun execute(currentAggregate: Dealer?): Result<Dealer> = when (currentAggregate) {
        is Dealer -> runCatching {
            currentAggregate.dealCardToDiscardDeck(gameIdentity)
        }

        else -> Result.failure(UnsupportedOperationException("Aggregate in a wrong status"))
    }
}
