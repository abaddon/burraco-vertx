package com.abaddon83.burraco.dealer.commands

import com.abaddon83.burraco.dealer.models.Dealer
import com.abaddon83.burraco.dealer.models.DealerIdentity
import com.abaddon83.burraco.dealer.models.GameIdentity
import com.abaddon83.burraco.dealer.models.PlayerIdentity
import io.github.abaddon.kcqrs.core.domain.messages.commands.Command

data class CreateDecks(
    override val aggregateID: DealerIdentity,
    val gameIdentity: GameIdentity,
    val players: List<PlayerIdentity>
) : Command<Dealer>(aggregateID) {

    override fun execute(currentAggregate: Dealer?): Dealer = when (currentAggregate) {
        is Dealer -> currentAggregate.createDeck(aggregateID, gameIdentity, players)
        else -> throw UnsupportedOperationException("Aggregate in a wrong status")
    }

}
