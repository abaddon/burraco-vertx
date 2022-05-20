package com.abaddon83.burraco.dealer.commands

import com.abaddon83.burraco.dealer.models.Dealer
import com.abaddon83.burraco.dealer.models.DealerIdentity
import com.abaddon83.burraco.dealer.models.GameIdentity
import io.github.abaddon.kcqrs.core.domain.messages.commands.Command

data class DealCardToPlayerDeck1(
    override val aggregateID: DealerIdentity,
    val gameIdentity: GameIdentity
) : Command<Dealer>(aggregateID) {

    override fun execute(currentAggregate: Dealer?): Dealer = when (currentAggregate) {
        is Dealer -> currentAggregate.dealCardToPlayerDeck1(gameIdentity)
        else -> throw UnsupportedOperationException("Aggregate in a wrong status")
    }

}
