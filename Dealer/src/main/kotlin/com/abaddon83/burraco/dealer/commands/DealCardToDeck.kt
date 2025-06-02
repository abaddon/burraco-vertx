package com.abaddon83.burraco.dealer.commands

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.dealer.models.Dealer
import com.abaddon83.burraco.common.models.DealerIdentity
import io.github.abaddon.kcqrs.core.domain.messages.commands.Command

data class DealCardToDeck(
    override val aggregateID: DealerIdentity,
    val gameIdentity: GameIdentity
) : Command<Dealer>(aggregateID) {

    override fun execute(currentAggregate: Dealer?): Result<Dealer> = when (currentAggregate) {
        is Dealer -> runCatching {
            currentAggregate.dealCardToDeck(gameIdentity)
        }

        else -> Result.failure(UnsupportedOperationException("Aggregate in a wrong status"))
    }

}
